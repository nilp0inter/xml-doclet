import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

class CustomVersionTask extends DefaultTask {
    private final ExecOperations execOperations

    @Inject
    CustomVersionTask(ExecOperations execOperations) {
        this.execOperations = execOperations
    }

    @TaskAction
    void printVersion() {
        def version = getVersion(execOperations, true)
        println "Project version: $version"
    }


    def getVersion = { ExecOperations execOperations, boolean considerSnapshot ->
        Integer major = 0
        Integer minor = 0
        Integer patch = null
        Integer build = null
        def commit = null
        def snapshot = ""
        new ByteArrayOutputStream().withStream { os ->
            // TODO: use ExecOperations.exec
            execOperations.exec { spec ->
                spec.args = [
                        "--no-pager"
                        , "describe"
                        , "--tags"
                        , "--always"
                        , "--dirty=-SNAPSHOT"
                ]
                spec.executable "git"
                spec.standardOutput = os
            }
            def versionStr = os.toString().trim()
            def pattern = /(?<major>\d*)\.(?<minor>\d*)(\.(?<patch>\d*))?(-(?<build>\d*)-(?<commit>[a-zA-Z\d]*))?/
            def matcher = versionStr =~ pattern
            if (matcher.find()) {
                major = matcher.group('major') as Integer
                minor = matcher.group('minor') as Integer
                patch = matcher.group('patch') as Integer
                build = matcher.group('build') as Integer
                commit = matcher.group('commit')
            }

            if (considerSnapshot && ( versionStr.endsWith('SNAPSHOT') || build!=null) ) {
                minor++
                if (patch!=null) patch = 0
                snapshot = "-SNAPSHOT"
            }
        }
        return patch!=null
                ? "${major}.${minor}.${patch}${snapshot}"
                :  "${major}.${minor}${snapshot}"
    }
}
