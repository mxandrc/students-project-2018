def CONTAINER_NAME = "app"

node {
    stage('Initialize') {
       	def dockerHome = tool 'myDocker'
       	env.PATH = "${dockerHome}/bin:${env.PATH}"
		echo "Initialize"
    }

    stage('Checkout') {
        deleteDir()
        checkout scm
		echo "Checkout"
    }

    stage('Build') {
        
		TAG = sh(returnStdout: true, script: "git describe --tags").trim()
		if (TAG == '') {
			echo "Tag is empty"
			currentBuild.result = 'FAILURE'
			}
			
		try {
			sh "docker build -t $CONTAINER_NAME:$TAG --pull --no-cache ."
			currentBuild.result = 'SUCCESS'
			echo "Build"
        } catch (error) {
                currentBuild.result = 'FAILURE'
				echo "Not build"
		}
    }

    stage('Unit tests') {

		try {
			sh "docker run --rm --name $CONTAINER_NAME $CONTAINER_NAME:$TAG"
			sleep 10
			sh "docker exec $CONTAINER_NAME python greetings_app/test_selects.py"
			status = sh(returnStdout: true, script: "echo $?").trim()
				if (status != '0'){
				currentBuild.result = 'FAILED'
				sh "exit ${status}"
				}
			echo "Test success"
			currentBuild.result = 'SUCCESS'
		} catch (error) {
			echo "Test failure"
			currentBuild.result = 'FAILURE'
		}
    }

    stage('Push to DockerHub') {
		try {
			withCredentials([usernamePassword(credentialsId: 'mxandrc', passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
					sh "docker login -u $USER -p $PASSWORD"
					sh "docker tag $CONTAINER_NAME:$TAG $CONTAINER_NAME:$TAG"
					sh "docker push $CONTAINER_NAME:$CURRENT_TAG"
			}
			echo "Image push complete"
			currentBuild.result = 'SUCCESS'
		} catch (error) {
			echo "Image not push"
			currentBuild.result = 'FAILURE'
		}
	}

}
