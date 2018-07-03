package com.blackbaud.templates.tasks

import com.google.common.base.CaseFormat


class AsyncProject {

    private BasicProject basicProject

    AsyncProject(BasicProject basicProject) {
        this.basicProject = basicProject
    }

    String getServiceName() {
        basicProject.serviceName
    }

    String getServicePackage() {
        basicProject.servicePackage
    }

    String getServicePackagePath() {
        basicProject.servicePackagePath
    }

    private void initServiceBusIfNotAlreadyInitialized() {
        if (basicProject.getBuildFile().text =~ /common-async-service-bus/) {
            return
        }

        FileUtils.appendAfterLastLine(basicProject.getBuildFile(), /ext \{/,
                '        commonAsyncServiceBusVersion = "2.+"')
        FileUtils.appendAfterLine(basicProject.getBuildFile(), /compile.*common-spring-boot/,
                '    compile "com.blackbaud:common-async-service-bus:${commonAsyncServiceBusVersion}"')
        FileUtils.appendAfterLine(basicProject.getBuildFile(), /sharedTestCompile/,
                '    sharedTestCompile "com.blackbaud:common-async-service-bus-test:${commonAsyncServiceBusVersion}"')

        File applicationPropertiesFile = basicProject.getProjectFile("src/main/resources/application-local.properties")
        if ((applicationPropertiesFile.exists() && applicationPropertiesFile.text.contains("servicebus.stub")) == false) {
            applicationPropertiesFile.append("""
servicebus.stub=true
""")
        }

        basicProject.applyTemplate("src/main/java/${servicePackagePath}/servicebus") {
            "ServiceBusConfig.java" template: "/templates/springboot/service-bus/service-bus-config.java.tmpl",
                                    packageName: "${servicePackage}.servicebus"
        }
    }

    void addInternalTopic(String topicName, boolean sessionEnabled) {
        addTopic(topicName, true, true, true, sessionEnabled)
    }

    void addExternalTopic(String topicName, boolean consumer, boolean publisher, boolean sessionEnabled) {
        addTopic(topicName, false, consumer, publisher, sessionEnabled)
    }

    private void addTopic(String topicName, boolean internal, boolean consumer, boolean publisher, boolean sessionEnabled) {
        initServiceBusIfNotAlreadyInitialized()

        ServiceBusNameResolver formatter = new ServiceBusNameResolver(topicName)
        File componentTestConfigFile = basicProject.findComponentTestConfig()

        File applicationPropertiesFile = basicProject.getProjectFile("src/main/resources/application-local.properties")
        if (applicationPropertiesFile.text.contains("servicebus.namespace") == false) {
            applicationPropertiesFile.append("""
servicebus.namespace=namespace
""")
        }
        applicationPropertiesFile.append("""\
servicebus.${formatter.topicNameSnakeCase}.entity_path=${formatter.topicNameSnakeCase}
servicebus.${formatter.topicNameSnakeCase}.shared_access_key_name=keyName
servicebus.${formatter.topicNameSnakeCase}.shared_access_key=key
""")
        if (consumer) {
            applicationPropertiesFile.append("""\
servicebus.${formatter.topicNameSnakeCase}.subscription=consumer
""")
        }

        basicProject.applyTemplate("src/main/java/${servicePackagePath}/servicebus") {
            "${formatter.propertiesClassName}.java" template: "/templates/springboot/service-bus/service-bus-properties.java.tmpl",
                                                    packageName: "${servicePackage}.servicebus",
                                                    className: formatter.propertiesClassName,
                                                    topicPrefix: formatter.topicNameCamelCase
        }

        File serviceBusConfigFile = basicProject.findFile("ServiceBusConfig.java")
        File publisherConfigFile
        if (publisher) {
            publisherConfigFile = serviceBusConfigFile
            if (internal) {
                basicProject.addInternalApiObject("service-bus", formatter.payloadClassName, false)
            } else {
                basicProject.addExternalApiObject("service-bus", formatter.payloadClassName, false)
            }
        } else {
            publisherConfigFile = componentTestConfigFile
            FileUtils.addImport(publisherConfigFile, "${servicePackage}.servicebus.${formatter.propertiesClassName}")
        }

        FileUtils.addImport(publisherConfigFile, "org.springframework.beans.factory.annotation.Qualifier")
        FileUtils.addImport(publisherConfigFile, "com.blackbaud.azure.servicebus.publisher.JsonMessagePublisher")
        FileUtils.addImport(publisherConfigFile, "com.blackbaud.azure.servicebus.config.ServiceBusProperties")
        FileUtils.addImport(publisherConfigFile, "com.blackbaud.azure.servicebus.publisher.ServiceBusPublisherBuilder")
        FileUtils.appendToClass(publisherConfigFile, """
    @Bean
    public JsonMessagePublisher ${formatter.topicNameCamelCase}Publisher(
            ServiceBusPublisherBuilder.Factory serviceBusPublisherFactory,
            @Qualifier("${formatter.topicNameCamelCase}ServiceBusProperties") ServiceBusProperties serviceBusProperties) {
        return serviceBusPublisherFactory.create()
                .buildJsonPublisher(serviceBusProperties);
    }
""")

        if (consumer) {
            basicProject.applyTemplate("src/main/java/${servicePackagePath}/servicebus") {
                "${formatter.messageHandlerClassName}.java" template: "/templates/springboot/service-bus/message-handler.java.tmpl",
                                                            packageName: "${servicePackage}.servicebus",
                                                            className: formatter.messageHandlerClassName,
                                                            payloadClassName: formatter.payloadClassName
            }

            addConsumerImports(serviceBusConfigFile)
            FileUtils.appendToClass(serviceBusConfigFile, """
    @Bean
    public ${formatter.messageHandlerClassName} ${formatter.topicNameCamelCase}MessageHandler() {
        return new ${formatter.messageHandlerClassName}();
    }

    @Bean
    public ServiceBusConsumer ${formatter.topicNameCamelCase}Consumer(
            ServiceBusConsumerBuilder.Factory serviceBusConsumerFactory,
            ${formatter.messageHandlerClassName} ${formatter.topicNameCamelCase}MessageHandler,
            ${formatter.propertiesClassName} serviceBusProperties) {
        return serviceBusConsumerFactory.create()
                .serviceBus(serviceBusProperties)
                .jsonMessageHandler(${formatter.topicNameCamelCase}MessageHandler, ${formatter.payloadClassName}.class, ${sessionEnabled})
                .build();
    }
""")
        } else {
            addConsumerImports(componentTestConfigFile)
            FileUtils.addImport(componentTestConfigFile, "com.blackbaud.azure.servicebus.consumer.handlers.ValidatingServiceBusMessageHandler")
            FileUtils.appendToClass(componentTestConfigFile, """
    @Bean
    public ValidatingServiceBusMessageHandler<${formatter.payloadClassName}> ${formatter.messageHandlerClassName}() {
        return new ValidatingServiceBusMessageHandler<>("${formatter.topicNameCamelCase}Handler");
    }

    @Bean
    public ServiceBusConsumer sessionConsumer(
            ServiceBusConsumerBuilder.Factory serviceBusConsumerFactory,
            ${formatter.propertiesClassName} serviceBusProperties,
            @Qualifier("${formatter.topicNameCamelCase}MessageHandler") ValidatingServiceBusMessageHandler<${formatter.payloadClassName}> messageHandler) {
        return serviceBusConsumerFactory.create()
                .serviceBus(serviceBusProperties)
                .jsonMessageHandler(messageHandler, ${formatter.payloadClassName}.class, ${sessionEnabled})
                .build();
    }
""")
        }
    }

    private void addConsumerImports(File configFile) {
        FileUtils.addImport(configFile, "org.springframework.beans.factory.annotation.Qualifier")
        FileUtils.addImport(configFile, "com.blackbaud.azure.servicebus.config.ServiceBusProperties")
        FileUtils.addImport(configFile, "com.blackbaud.azure.servicebus.consumer.ServiceBusConsumer")
        FileUtils.addImport(configFile, "com.blackbaud.azure.servicebus.consumer.ServiceBusConsumerBuilder")
    }

    private static class ServiceBusNameResolver {
        String topicNameCamelCase
        String topicNameSnakeCase

        ServiceBusNameResolver(String topicName) {
            if (topicName.contains("_")) {
                topicNameSnakeCase = topicName
                topicNameCamelCase = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, topicName)
            } else {
                if (Character.isLowerCase(topicName.charAt(0))) {
                    topicNameCamelCase = topicName
                    topicNameSnakeCase = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, topicName)
                } else {
                    topicNameCamelCase = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_CAMEL, topicName)
                    topicNameSnakeCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, topicName)
                }
            }
        }

        String getMessageHandlerClassName() {
            "${topicNameCamelCase.capitalize()}MessageHandler"
        }

        String getPayloadClassName() {
            "${topicNameCamelCase.capitalize()}Payload"
        }

        String getPropertiesClassName() {
            "${topicNameCamelCase.capitalize()}ServiceBusProperties"
        }

    }

}
