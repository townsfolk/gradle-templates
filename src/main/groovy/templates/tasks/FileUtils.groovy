package templates.tasks


class FileUtils {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator")

    static void appendToClass(File classFile, String textToAppend) {
        classFile.text = classFile.text.replaceFirst(/(?ms)\s*}\s*(?!.*?\s*}\s*)/, """${textToAppend}
}
""")
    }

    static void appendAfterLine(File file, String match, String lineToAdd) {
        List<String> lines = file.readLines()

        for (int i = 0; i < lines.size(); i++) {
            if (lines[i] =~ /${match}/) {
                lines.add(i + 1, lineToAdd)
                break
            }
        }
        file.text = lines.join(LINE_SEPARATOR)
    }

}
