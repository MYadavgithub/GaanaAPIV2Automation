/* groovylint-disable NoDef */
/* groovylint-disable-next-line CompileStatic */
pipeline {
    agent any

    environment {
        /* groovylint-disable-next-line UnusedVariable, VariableTypeRequired */
        def mvnHome = tool name: 'Maven', type: 'maven'
        def emailTo = "umeshshukla195@gmail.com"
        def replyTo = "umesh.shukla@gaana.com"
        def emailSub = "Staging | CDN | Tencent Migration Test Suite along with Akamai | "
    }

    parameters
    {
        string(
            defaultValue: 'master',
            description: 'Provide branch for execution',
            name: 'branch',
            trim: true
        )
        string(
            defaultValue: 'reco_regression.xml',
            description: 'Enter your suite xml file which you want to execute.',
            name: 'suiteXmlFile',
            trim: true
        )
        choice(
            choices: ['production', 'local', 'preprod'],
            description: 'Select environment according to your execution requirement.',
            name: 'env'
        )
        choice(
            choices: ['Reco', 'Search'],
            description: 'Select whether you want to execute on Recommendation module or Search.',
            name: 'type'
        )
        choice(
            choices: ['Android', 'Ios', 'Web'],
            description: 'Please select device type, for api header segregation.',
            name: 'device_type'
        )
    }

    options {
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                sh 'git stash'
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '$branch']],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [],
                    submoduleCfg: [],
                    userRemoteConfigs: [
                        [
                            credentialsId: '23aea9c9-d673-4f84-a417-aa6efc2f921e',
                            url: 'git@bitbucket.org:umesh_qa/ggm_api.git'
                        ]
                    ]
                ])
            }
        }

        stage ('Build') {
            steps {
                sh "${mvnHome}/bin/mvn -version"
                sh "${mvnHome}/bin/mvn -B -DskipTests clean package"
                sh "chmod 777 -R ${env.WORKSPACE}/pom.xml"
            }
        }

        stage ('Execute Tests') {
            steps {
                /* groovylint-disable-next-line LineLength */
                /* groovylint-disable-next-line ConsecutiveStringConcatenation, GStringExpressionWithinString, LineLength */
                sh "mvn -B -f ${env.WORKSPACE}/pom.xml clean install " + '-Denv=${env} -Dtype=${type} -Ddevice_type=${device_type} -DsuiteXmlFile=${suiteXmlFile}'
            }
        }
    }

    post {
        always {
            script {
                def workspace = env.WORKSPACE
            }
            emailext attachLog: true,
            to: "${emailTo}",
            replyTo: "${replyTo}",
            subject: "${emailSub}"+'$DEFAULT_SUBJECT',
            /* groovylint-disable-next-line GStringExpressionWithinString */
            body: '''
            ${FILE, path = "/var/lib/jenkins/workspace/pipeLine_build/Reports/EmailerReport.html"}<br>
            ${SCRIPT, template="groovy-html.template"}
            <b><i>For Detailed information please download extent report from attachement.</i></b><br>
            Both Tencent and Akamai working as expected for shown consumer types in report.''',
            mimeType: 'text/html',
            compressLog: true,
            attachmentsPattern: '**/Reports/*ExtentReport.html'
        }
    }
}
