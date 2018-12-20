package com.blackbaud.templates


class CurrentVersions {

    static final String SPRING_BOOT_VERSION = "2.1.1.RELEASE"
    static final String GROOVY_VERSION = "2.4.15"
    static final String LOMBOK_VERSION = "1.18.4"
    static final String SPOCK_VERSION = "1.2-groovy-2.4"
    static final String GUAVA_VERSION = "27.0.1-jre"

    static final String COMMON_DEPLOYABLE_MAJOR_VERSION = "0"
    static final String COMMON_TEST_MAJOR_VERSION = "4"
    static final String COMMON_GEB_MAJOR_VERSION = "3"
    static final String COMMON_SERVICE_BUS_MAJOR_VERSION = "3"
    static final String COMMON_EVENT_HUBS_MAJOR_VERSION = "4"
    static final String COMMON_KAFKA_API_MAJOR_VERSION = "2"
    static final String COMMON_KAFKA_MAJOR_VERSION = "4"
    static final String GRADLE_INTERNAL_MAJOR_VERSION = "5"
    static final String GRADLE_TEMPLATES_MAJOR_VERSION = "4"
    static final String TOKENS_CLIENT_MAJOR_VERSION = "3"

    static Map<String, String> VERSION_MAP = [:]

    static {
        VERSION_MAP.put("springBootVersion", SPRING_BOOT_VERSION)
        VERSION_MAP.put("groovyVersion", GROOVY_VERSION)
        VERSION_MAP.put("lombokVersion", LOMBOK_VERSION)
        VERSION_MAP.put("spockVersion", SPOCK_VERSION)
        VERSION_MAP.put("guavaVersion", GUAVA_VERSION)

        VERSION_MAP.put("commonDeployableMajorVersion", COMMON_DEPLOYABLE_MAJOR_VERSION)
        VERSION_MAP.put("commonTestMajorVersion", COMMON_TEST_MAJOR_VERSION)
        VERSION_MAP.put("commonGebMajorVersion", COMMON_GEB_MAJOR_VERSION)
        VERSION_MAP.put("commonServiceBusMajorVersion", COMMON_SERVICE_BUS_MAJOR_VERSION)
        VERSION_MAP.put("commonEventHubsMajorVersion", COMMON_EVENT_HUBS_MAJOR_VERSION)
        VERSION_MAP.put("commonKafkaMajorVersion", COMMON_KAFKA_MAJOR_VERSION)
        VERSION_MAP.put("commonKafkaApiMajorVersion", COMMON_KAFKA_API_MAJOR_VERSION)
        VERSION_MAP.put("gradleInternalMajorVersion", GRADLE_INTERNAL_MAJOR_VERSION)
        VERSION_MAP.put("gradleTemplatesMajorVersion", GRADLE_TEMPLATES_MAJOR_VERSION)
    }

}
