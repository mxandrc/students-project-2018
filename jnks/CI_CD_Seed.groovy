pipelineJob("CI-job") {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/mxandrc/students-project-2018.git')
                        credentials('mxandrc')
                    }
                    branch('.git/refs/tags/*.*')
                }
            }
            scriptPath("jnks/CI_job.groovy")
        }
    }
	triggers {
        scm('H/1 * * * *')
    }
}
