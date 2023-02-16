package com.example.step2.apicall;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ApiClient {

	public static CompletionStage<Optional<Item>> fetchItem(long itemId) {
		return CompletableFuture.completedFuture(Optional.of(new Item("foo", itemId)));
	}
}
