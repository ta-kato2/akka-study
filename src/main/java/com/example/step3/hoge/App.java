package com.example.step3.hoge;

import static akka.http.javadsl.server.PathMatchers.longSegment;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
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
		return concat(get(() -> pathPrefix("item", () -> path(longSegment(), (Long id) -> {
			final CompletionStage<Optional<Item>> futureMaybeItem = ApiClient.fetchItem(id);
			return onSuccess(futureMaybeItem, maybeItem -> {
				return maybeItem.map(item -> {
					return completeOK(item, Jackson.marshaller());
				}).orElseGet(() -> {
					return complete(StatusCodes.NOT_FOUND, "Not Found");
				});
			});
		}))));
	}
}
