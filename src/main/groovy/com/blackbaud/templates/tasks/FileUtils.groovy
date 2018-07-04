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

    static def addConfigurationImport(File file, String importToAdd) {
        appendBeforeLine(file, /class\s+/, "@Import(${importToAdd})")
    }

    static void appendBeforeLine(File file, String match, String lineToAdd) {
        List<String> lines = file.readLines()

        for (int i = 0; i < lines.size(); i++) {
            if (lines[i] =~ /${match}/) {
                lines.add(i, lineToAdd)
                break
            }
        }
        file.text = lines.join(LINE_SEPARATOR) + LINE_SEPARATOR
    }

    static void appendAfterLine(File file, String match, String lineToAdd) {
        List<String> lines = file.readLines()

        for (int i = 0; i < lines.size(); i++) {
            if (lines[i] =~ /${match}/) {
                lines.add(i + 1, lineToAdd)
                break
            }
        }
        file.text = lines.join(LINE_SEPARATOR) + LINE_SEPARATOR
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
