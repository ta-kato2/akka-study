package com.example.step1.simpletext;

import java.util.concurrent.CompletionStage;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

public class App extends AllDirectives {

	public static void main(String[] args) throws Exception {

		ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "routes");

		App app = new App();

		Http http = Http.get(system);
		CompletionStage<ServerBinding> binding = http.newServerAt("localhost", 8080).bind(app.createRoute());
		System.out.println("Server online at http://localhost:8080");
		System.in.read();

		binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
	}

	private Route createRoute() {
		return concat( //
				path("hello", () -> {
					return concat( //
							get(() -> {
								return complete("<h1>ゲッツ！</h1>");
							}), //
							put(() -> {
								return complete("<h1>プッツ！</h1>");
							}) //
					);
				}), //
				path("hoge", () -> {
					return concat( //
							get(() -> {
								return complete("<h1>ホゲゲッツ！</h1>");
							}), //
							put(() -> {
								return complete("<h1>ホゲプッツ！</h1>");
							}) //
					);
				}) //
		);
	}
}
