package com.example.step4.octopus.http;

import static akka.actor.typed.javadsl.AskPattern.ask;
import static akka.http.javadsl.server.PathMatchers.segment;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import com.example.step4.octopus.actor.prefixcall.PrefixCallMessage;
import com.example.step4.octopus.actor.prefixcall.message.CreatePrefixCallMessage;
import com.example.step4.octopus.actor.prefixcall.message.ReadPrefixCallMessage;
import com.example.step4.octopus.http.request.CreatePrefixCallRequst;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpEntity;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.unmarshalling.Unmarshaller;

public class Router extends AllDirectives {

	private final Unmarshaller<HttpEntity, CreatePrefixCallRequst> createRequestParser = Jackson
			.unmarshaller(CreatePrefixCallRequst.class);

	public Route createRoute(ActorSystem<PrefixCallMessage> system) {
		return concat(//
				path("prefix-call", () -> concat(//
						post(() -> concat(entity(createRequestParser, requestBody -> {
							system.tell(createCreatePrefixCallMessage(requestBody));
							return complete(StatusCodes.ACCEPTED);
						}))) //
				)), //
				pathPrefix("prefix-call", () -> path(segment(), (String id) -> {
					// GETは依頼先のActorの処理が終わるのを待って、レスポンスを返している。
					// CQRSでやるなら、Readはこういう形ではやらずに、別途ReadModelを参照するAPIを非Akkaで作るべきなのだろうなー
					// そもそも処理終わるの待っていたら、RPCと同じになっちゃうし。
					CompletionStage<ReadPrefixCallMessage> result = ask(system,
							callerRef -> createReadPrefixCallMessage(id, callerRef), Duration.ofSeconds(5),
							system.scheduler());
					return completeOKWithFuture(result, Jackson.marshaller());
				})));
	}

	private CreatePrefixCallMessage createCreatePrefixCallMessage(CreatePrefixCallRequst request) {
		CreatePrefixCallMessage message = new CreatePrefixCallMessage();
		message.msisdn = request.msisdn;
		return message;
	}

	private ReadPrefixCallMessage createReadPrefixCallMessage(String id, ActorRef<ReadPrefixCallMessage> callerRef) {
		ReadPrefixCallMessage message = new ReadPrefixCallMessage();
		message.id = id;
		message.callerRef = callerRef;
		return message;
	}
}
