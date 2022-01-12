#! /bin/groovy
/*
    The methods in this file use values for GIT data that are sent in by an upstream, traditional Jenkins job.
    The traditional job - freestyle job - has access to the GIT environment variables but not the pipeline job.
    This will work for deployment models where upstream and downstream jobs are called for specific pipeline integration.

    If all the jobs are of pipeline type, these methods will not work and regular git commands will have to be used as done in original IO library.
*/


/**
* Get all Git data and checkout code

**/
def getGitRepoInfo() {

    def gitData = [:]
    UtilPrint utilPrint = new UtilPrint()
    utilPrint.sectionSeperator("Getting SCM information")

    print "${GITURL} branch: ${GITBRANCH}"
    
    gitData.gitUrl = GITURL
    gitData.gitBranch = GITBRANCH
    // gitData.committer = GITCOMMITTER

    // gitData.repoOwner = REPOOWNER    

    if(gitUrl ==~ /.*git@.*/) {
        gitData.repoOwner = gitUrl.tokenize('/')[1]
        gitData.repoName = gitUrl.tokenize('/').last().split("\\.")[0]
    } else {
        gitData.repoOwner = gitUrl.replace("https://", "").tokenize('/')[1]
        gitData.repoName = gitUrl.tokenize('/').last().split("\\.")[0]
    }

    if(gitUrl ==~ /.*bitbucket.org.*/) {
        gitData.scmName = "bitbucket"

    }else if(gitUrl ==~ /.*github.com.*/) {
        gitData.scmName = "github"

    } else if(gitUrl ==~ /.*gitlab.*/) {
        gitData.scmName = "gitlab"
    } else {
        gitData.scmName = "external"
    }

    return gitData    
}

def gitCheckout(gitData) {
    //Checkout the code
    checkout([$class: 'GitSCM', branches: [[name: gitData.gitBranch]], extensions: [], userRemoteConfigs: [[credentialsId: 'Github-AuthToken', url: gitData.gitUrl]]])
    //git branch: gitData.gitBranch, credentialsId: 'Github-AuthToken', url: gitData.gitUrl
}

// Not applicable to FPT since using scm: gitlab ?
// def getCodePatch() {
//     def shortstat = sh label: 'patch', returnStdout: true, script: 'git log --shortstat -1'
//     def changes = sh label: 'patch', returnStdout: true, script: 'git whatchanged -p -1'
    
//     codePatchString = shortstat+changes
//     print "${codePatchString}"
//     return codePatchString
// }