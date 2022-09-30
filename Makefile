docker-build:
	docker build --no-cache -t uartweb/auth .

docker-run:
	echo "Running on port 8080"
	docker run --env-file /home/seaeagle/Desktop/uart_auth.env -p 8080:8080 --net uart_net --name uart-auth -d uartweb/auth
docker-stop:
	docker stop uart-auth

docker-remove:
	make docker-stop
	docker rm uart-auth

docker-push:
	make docker-build
	docker push uartweb/auth:latest

docker-deploy:
	make docker-build
	make docker-run


