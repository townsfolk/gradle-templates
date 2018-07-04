package com.blackbaud.templates.tasks


class FileUtils {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator")

    static void appendToClass(File classFile, String textToAppend) {
        StringBuilder builder = new StringBuilder(classFile.text)
        builder.insert(classFile.text.lastIndexOf("}") - 1, textToAppend)
        classFile.text = builder.toString()
    }

    static void addImport(File file, String importToAdd) {
        if (file.text.contains("import ${importToAdd}") == false) {
            appendBeforeLine(file, /import .*/, "import ${importToAdd};")
        }
    }

    static void addConfigurationImport(File file, String importToAdd) {
        List<String> lines = file.readLines()
        int index = indexOf(lines, /@Import/)

        if (index >= 0) {
            String importLine = lines[index]
            String imports = (importLine =~ /@Import\(\{?([^}]+)\}?\)/)[0][1]
            lines[index] = "@Import({${imports}, ${importToAdd}})".toString()
            file.text = lines.join(LINE_SEPARATOR) + LINE_SEPARATOR
        } else {
            appendBeforeLine(file, /class\s+/, "@Import(${importToAdd})")
        }
    }

    private static int indexOf(List<String> lines, String match) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines[i] =~ /${match}/) {
                return i
            }
        }
        return -1
    }

    static void replaceLine(File file, String match, String lineToReplace) {
        List<String> lines = file.readLines()
        int index = indexOf(lines, match)

        if (index >= 0) {
            lines[index] = lineToReplace
            file.text = lines.join(LINE_SEPARATOR) + LINE_SEPARATOR
        }
    }

    static void appendBeforeLine(File file, String match, String lineToAdd) {
        List<String> lines = file.readLines()
        int index = indexOf(lines, match)

        if (index >= 0) {
            lines.add(index, lineToAdd)
            file.text = lines.join(LINE_SEPARATOR) + LINE_SEPARATOR
        }
    }

    static void appendAfterLine(File file, String match, String lineToAdd) {
        List<String> lines = file.readLines()
        int index = indexOf(lines, match)

        if (index >= 0) {
            lines.add(index + 1, lineToAdd)
            file.text = lines.join(LINE_SEPARATOR) + LINE_SEPARATOR
        }
    }

    static void appendAfterLastLine(File file, String match, String lineToAdd) {
        List<String> lines = file.readLines()
        int index = 0

        for (int i = 0; i < lines.size(); i++) {
            if (lines[i] =~ /${match}/) {
                index = i
            }
        }
        lines.add(index + 1, lineToAdd)

        file.text = lines.join(LINE_SEPARATOR) + LINE_SEPARATOR
    }

}
