package com.blackbaud.templates.project

class BuildFile extends ProjectFile {

    BuildFile(File file) {
        super(file)
    }

    void applyPlugin(String pluginName) {
        appendAfterLine(/apply\s+plugin:\s+"blackbaud-internal/, /apply plugin: "${pluginName}"/)
    }

    void addDependency(String type, String dependency, String exclusion = null) {
        String dependencyString = makeDependencyString(type, dependency, exclusion)

        List<String> lines = readLines()
        boolean inBuildscript = false
        boolean inDependencies = false
        boolean inExclusion = false
        String previousDependencyType = ""
        for (int i = 0; i < lines.size(); i++) {
            String line = lines[i]
            if (isBlankLine(line)) {
                continue
            }
            if (isStartOfBuildscript(line)) {
                inBuildscript = true
                continue
            }
            if (isStartOfDependencies(line)) {
                inDependencies = true
                continue
            }
            if (!inBuildscript && inDependencies) {
                if (isStartOfClosure(line)) {
                    inExclusion = true
                }
                if (!inBuildscript && inDependencies && inExclusion && isEndOfClosure(line)) {
                    inExclusion = false
                    continue
                }
                String dependencyType = getDependencyType(line)
                if (!inExclusion && dependencyType != previousDependencyType && previousDependencyType == type) {
                    addDependencyAfterExistingDependencyType(dependencyString, lines, i)
                    break
                }
                if (isExclusion(line)) {
                    continue
                }
                previousDependencyType = dependencyType
            }
            if (inDependencies && isEndOfClosure(line)) {
                inDependencies = false
                if (!inBuildscript) {
                    addDependencyForNewType(dependencyString, lines, i)
                    break
                }
            }
            else if (!inDependencies && inBuildscript && lines[i] =~ /\}$/) {
                inBuildscript = false
            }
        }
    }

    private static String makeDependencyString(String type, String dependency, String exclusion) {
        return exclusion == null ?
                """    ${type} \"${dependency}\"""" :
                """    ${type} (\"${dependency}\") {
        exclude group: \"${exclusion}\"
    }"""
    }

    private static boolean isBlankLine(String line) {
        return line =~ /^\s*$/
    }

    private static boolean isStartOfBuildscript(String line) {
        return line =~ /buildscript/
    }

    private static boolean isStartOfDependencies(String line) {
        return line =~ /dependencies/
    }

    private static boolean isStartOfClosure(String line) {
        return line =~ /\{$/
    }

    private static boolean isEndOfClosure(String line) {
        return line =~/\}$/
    }

    private static boolean isExclusion(String line) {
        return line =~ /exclude/
    }

    private static String getDependencyType(String line) {
        return line.split()[0]
    }

    private void addDependencyForNewType(String dependencyString, List<String> lines, int index) {
        addDependencyString("\n${dependencyString}", lines, index)
    }

    private void addDependencyAfterExistingDependencyType(String dependencyString, List<String> lines, int index) {
        addDependencyString(dependencyString, lines, index)
    }

    private void addDependencyString(String dependencyString, List<String> lines, int index) {
        lines.add(index, dependencyString)
        text = lines.join(LINE_SEPARATOR)
    }
}
