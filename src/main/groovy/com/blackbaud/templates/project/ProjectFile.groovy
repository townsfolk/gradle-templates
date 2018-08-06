package com.blackbaud.templates.project


class ProjectFile extends File {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator")

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

    void addConfigurationImport(String importToAdd) {
        List<String> lines = readLines()
        int index = indexOf(lines, /@Import/)

        if (index >= 0) {
            String importLine = lines[index]
            String imports = (importLine =~ /@Import\(\{?([^}]+)\}?\)/)[0][1]
            lines[index] = "@Import({${imports}, ${importToAdd}})".toString()
            text = lines.join(LINE_SEPARATOR) + LINE_SEPARATOR
        } else {
            appendBeforeLine(/class\s+/, "@Import(${importToAdd})")
        }
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
        addProperty("${LINE_SEPARATOR}${key}", value)
    }

    void addProperty(String key, String value) {
        append("""${key}=${value}
""")
    }

}
