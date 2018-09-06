package com.blackbaud.templates.project

import com.blackbaud.templates.CurrentVersions
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
        BuildFile buildFile = basicProject.buildFile
        if (buildFile.text =~ /common-async-service-bus/) {
            return
        }

        buildFile.appendAfterLastLine(/ext \{/,
                "        commonAsyncServiceBusVersion = \"${CurrentVersions.COMMON_ASYNC_MAJOR_VERSION}.+\"")
        buildFile.appendAfterLine(/compile.*common-spring-boot/,
                '    compile "com.blackbaud:common-async-service-bus:${commonAsyncServiceBusVersion}"')
        buildFile.appendAfterLine(/sharedTestCompile/,
                '    sharedTestCompile "com.blackbaud:common-async-service-bus-test:${commonAsyncServiceBusVersion}"')

        ProjectFile applicationPropertiesFile = basicProject.getProjectFile("src/main/resources/application-local.properties")
        applicationPropertiesFile.addPropertyWithSeparator("servicebus.stub", "true")

        basicProject.applyTemplate("src/main/java/${servicePackagePath}/servicebus") {
            "ServiceBusConfig.java" template: "/templates/springboot/service-bus/service-bus-config.java.tmpl",
                                    packageName: "${servicePackage}.servicebus"
        }
    }

    void addInternalTopic(String topicName, TopicType topicType, boolean sessionEnabled) {
        addTopic(topicName, topicType, true, true, true, sessionEnabled)
    }

    void addExternalTopic(String topicName, TopicType topicType, boolean consumer, boolean publisher, boolean sessionEnabled) {
        addTopic(topicName, topicType, false, consumer, publisher, sessionEnabled)
    }

    private void addTopic(String topicName, TopicType topicType, boolean internal, boolean consumer, boolean publisher, boolean sessionEnabled) {
        initServiceBusIfNotAlreadyInitialized()

        ServiceBusNameResolver formatter = new ServiceBusNameResolver(topicName)
        ProjectFile componentTestConfigFile = basicProject.findComponentTestConfig()

        ProjectFile applicationPropertiesFile = basicProject.getProjectFile("src/main/resources/application.properties")
        applicationPropertiesFile.addProperty("servicebus.${formatter.topicNameSnakeCase}.session_enabled", "${sessionEnabled}")

        ProjectFile applicationLocalPropertiesFile = basicProject.getProjectFile("src/main/resources/application-local.properties")
        applicationLocalPropertiesFile.addProperty("servicebus.${formatter.topicNameSnakeCase}.producer_connection_url",
                                                   "Endpoint=sb://test.servicebus.windows.net/;SharedAccessSignature=SharedAccessSignature sr=amqp%3A%2F%2Ftest.servicebus.windows.net%2Ftest&sig=test")
        applicationLocalPropertiesFile.addProperty("servicebus.${formatter.topicNameSnakeCase}.consumer_connection_url",
                                                   "Endpoint=sb://test.servicebus.windows.net/;SharedAccessSignature=SharedAccessSignature sr=amqp%3A%2F%2Ftest.servicebus.windows.net%2Ftest%2Fsubscriptions%2Ftest&sig=test")
        if (publisher) {
            applicationPropertiesFile.addProperty("servicebus.${formatter.topicNameSnakeCase}.producer_connection_url",
                                                  "\${APPSETTING_ServiceBus__${formatter.topicNameSnakeCase}__Send}")
        }
        if (consumer) {
            applicationPropertiesFile.addProperty("servicebus.${formatter.topicNameSnakeCase}.consumer_connection_url",
                                                  "\${APPSETTING_ServiceBus__${formatter.topicNameSnakeCase}__Listen}")
        }

        basicProject.applyTemplate("src/main/java/${servicePackagePath}/servicebus") {
            "${formatter.propertiesClassName}.java" template: "/templates/springboot/service-bus/service-bus-properties.java.tmpl",
                                                    packageName: "${servicePackage}.servicebus",
                                                    className: formatter.propertiesClassName,
                                                    topicPrefix: "servicebus.${formatter.topicNameSnakeCase}"
        }

        ProjectFile serviceBusConfigFile = basicProject.findFile("ServiceBusConfig.java")
        serviceBusConfigFile.addClassAnnotation("@EnableConfigurationProperties(${formatter.propertiesClassName}.class)")

        ProjectFile applicationClass = basicProject.findFile("${basicProject.serviceName}.java")
        applicationClass.addImport("${servicePackage}.servicebus.ServiceBusConfig")
        applicationClass.addImport("org.springframework.context.annotation.Import")
        applicationClass.addConfigurationImport("ServiceBusConfig.class")

        ProjectFile publisherConfigFile
        if (publisher) {
            publisherConfigFile = serviceBusConfigFile
            if (internal) {
                basicProject.addInternalApiObject("service-bus", formatter.payloadClassName, false)
            } else {
                basicProject.addExternalApiObject("service-bus", formatter.payloadClassName, false)
            }
        } else {
            publisherConfigFile = componentTestConfigFile
            publisherConfigFile.addImport("${servicePackage}.servicebus.${formatter.propertiesClassName}")
        }

        publisherConfigFile.addImport("com.blackbaud.azure.servicebus.publisher.JsonMessagePublisher")
        publisherConfigFile.addImport("com.blackbaud.azure.servicebus.publisher.ServiceBusPublisherBuilder")
        publisherConfigFile.appendToClass("""
    @Bean
    public JsonMessagePublisher ${formatter.topicNameCamelCase}Publisher(
            ServiceBusPublisherBuilder.Factory serviceBusPublisherFactory,
            ${formatter.propertiesClassName} serviceBusProperties) {
        return serviceBusPublisherFactory.create()${if (sessionEnabled) {"""
                .supportsSessions(${formatter.payloadClassName}::getId)"""} else { "" } }
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
            serviceBusConfigFile.appendToClass("""
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
                .${topicType.consumerBuilderMethodName}(serviceBusProperties)
                .jsonMessageHandler(${formatter.topicNameCamelCase}MessageHandler, ${formatter.payloadClassName}.class)
                .build();
    }
""")
        } else {
            addConsumerImports(componentTestConfigFile)
            componentTestConfigFile.addImport("com.blackbaud.azure.servicebus.consumer.handlers.ValidatingServiceBusMessageHandler")
            componentTestConfigFile.addImport("org.springframework.beans.factory.annotation.Qualifier")
            componentTestConfigFile.appendToClass("""
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
                .${topicType.consumerBuilderMethodName}(serviceBusProperties)
                .jsonMessageHandler(messageHandler, ${formatter.payloadClassName}.class)
                .build();
    }
""")
        }
    }

    private void addConsumerImports(ProjectFile configFile) {
        configFile.addImport("org.springframework.beans.factory.annotation.Qualifier")
        configFile.addImport("com.blackbaud.azure.servicebus.config.ServiceBusProperties")
        configFile.addImport("com.blackbaud.azure.servicebus.consumer.ServiceBusConsumer")
        configFile.addImport("com.blackbaud.azure.servicebus.consumer.ServiceBusConsumerBuilder")
    }

    private static class ServiceBusNameResolver {
        String topicNameCamelCase
        String topicNameSnakeCase

        ServiceBusNameResolver(String topicName) {
            if (topicName.contains("_")) {
                topicNameSnakeCase = topicName
                topicNameCamelCase = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, topicName)
            } else if (topicName.contains("-")) {
                topicNameSnakeCase = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_UNDERSCORE, topicName)
                topicNameCamelCase = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, topicName)
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


    enum TopicType {
        SCHEDULE("schedulingTopicServiceBus"), DATASYNC("dataSyncTopicServiceBus")

        private String consumerBuilderMethodName

        private TopicType(String consumerBuilderMethodName) {
            this.consumerBuilderMethodName = consumerBuilderMethodName
        }

        String getConsumerBuilderMethodName() {
            return consumerBuilderMethodName
        }

        static TopicType resolveFromString(String name) {
            TopicType topicType = values().find {
                name.equalsIgnoreCase(it.name())
            }
            if (topicType == null) {
                List<String> types = values().collect{it.name().toLowerCase()}
                throw new RuntimeException("No type matching '${name}', available types=${types}")
            }
            topicType
        }
    }

}
