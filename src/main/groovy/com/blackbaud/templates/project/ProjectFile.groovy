package com.blackbaud.templates.project


class ProjectFile extends File {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator")
    private static final String ANNOTATION_REGEX = /\s*\(\s*\{?([^})]+)\s*\}?\s*\)/

    ProjectFile(File file) {
        super(file.toURI())
    }

    private int indexOf(List<String> lines, String match) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines[i] =~ /${match}/) {
                return i
            }
        }
        return -1
    }

    private boolean containsLine(String match) {
        List<String> lines = readLines()
        int index = indexOf(lines, match)
        index >= 0
    }

    void addImport(String importToAdd) {
        if (text.contains("import ${importToAdd}") == false) {
            String eol = name.endsWith(".java") ? ";" : ""
            if (appendBeforeLine(/import .*/, "import ${importToAdd}${eol}") == false) {
                appendAfterLine(/package .*/, """
import ${importToAdd}${eol}""")
            }
        }
    }

    void addConfigurationImport(String qualifiedClassName) {
        addImport("org.springframework.context.annotation.Import")
        addImport(qualifiedClassName)

        int packageEnd = qualifiedClassName.lastIndexOf('.')
        String importToAdd = qualifiedClassName.substring(packageEnd + 1) + ".class"

        addClassToAnnotation("Import", importToAdd)
    }

    void enableConfigurationProperties(String classToAdd) {
        addClassToAnnotation("EnableConfigurationProperties", classToAdd)
    }

    void addEntityScanAndEnableJpaRepositories(String packageName) {
        addImport("org.springframework.boot.autoconfigure.domain.EntityScan")
        addImport("org.springframework.data.jpa.repository.config.EnableJpaRepositories")

        addClassToAnnotation("EntityScan", "\"${packageName}\"")
        addClassToAnnotation("EnableJpaRepositories", "\"${packageName}\"")
    }

    private void addClassToAnnotation(String annotationName, String classToAdd) {
        if (text.contains(/@${annotationName}/)) {
            List<String> existingAnnotations = extractExistingAnnotations(annotationName)
            if (existingAnnotations.contains(classToAdd) == false) {
                String annotationsToReplace = (existingAnnotations + classToAdd).collect {
                    "        ${it}"
                }.join(",${LINE_SEPARATOR}")

                this.text = text.replaceFirst(/@${annotationName}${ANNOTATION_REGEX}/,
                                              "@${annotationName}({${LINE_SEPARATOR}${annotationsToReplace}${LINE_SEPARATOR}})")
            }
        } else {
            appendBeforeLine(/class\s+/, "@${annotationName}(${classToAdd})")
        }
    }

    private List<String> extractExistingAnnotations(String annotationName) {
        String text = this.text.replaceAll(/\s+/, ' ')
        String regex = /.*@${annotationName}${ANNOTATION_REGEX}.*/
        def matcher = text =~ regex
        String annotations = matcher[0][1]
        annotations.split(/\s*,\s*/).collect { it.trim() }
    }

    boolean addClassAnnotation(String annotation) {
        appendBeforeLine("public class", annotation)
    }

    void appendToClass(String textToAppend) {
        StringBuilder builder = new StringBuilder(text)
        builder.insert(text.lastIndexOf("}") - 1, textToAppend)
        text = builder.toString()
    }

    boolean appendAfterLine(String match, String lineToAdd) {
        List<String> lines = readLines()
        int index = indexOf(lines, match)

        if (index >= 0) {
            lines.add(index + 1, lineToAdd)
            text = lines.join(LINE_SEPARATOR) + LINE_SEPARATOR
        }
        index >= 0
    }

    boolean appendAfterFirstSetOfLines(String match, String lineToAdd) {
        List<String> lines = readLines()
        int index = indexOf(lines, match)

        if (index >= 0) {
            for (int i = index + 1; i < lines.size(); i++) {
                if (lines[i] =~ /${match}/) {
                    index = i
                }
                if (index != i) {
                    lines.add(i, lineToAdd)
                    break
                }
            }
            text = lines.join(LINE_SEPARATOR) + LINE_SEPARATOR
        }
    }

    void appendAfterLastLine(String match, String lineToAdd) {
        List<String> lines = readLines()
        int index = 0

        for (int i = 0; i < lines.size(); i++) {
            if (lines[i] =~ /${match}/) {
                index = i
            }
        }
        lines.add(index + 1, lineToAdd)

        text = lines.join(LINE_SEPARATOR) + LINE_SEPARATOR
    }

    boolean appendBeforeLine(String match, String lineToAdd) {
        List<String> lines = readLines()
        int index = indexOf(lines, match)

        if (index >= 0) {
            lines.add(index, lineToAdd)
            text = lines.join(LINE_SEPARATOR) + LINE_SEPARATOR
        }
        index >= 0
    }

    boolean replaceLine(String match, String lineToReplace) {
        List<String> lines = readLines()
        int index = indexOf(lines, match)

        if (index >= 0) {
            lines[index] = lineToReplace
            text = lines.join(LINE_SEPARATOR) + LINE_SEPARATOR
        }
        index >= 0
    }

    void addPropertyWithSeparator(String key, String value) {
        if (exists()) {
            if (text.contains("${key}=${value}") == false) {
                addProperty("${LINE_SEPARATOR}${key}", value)
            }
        } else {
            addProperty("${LINE_SEPARATOR}${key}", value)
        }
    }

    void addProperty(String key, String value) {
        if (exists() && text.endsWith(LINE_SEPARATOR) == false) {
            append(LINE_SEPARATOR)
        }
        append("""${key}=${value}
""")
    }

}
