package com.example.step4.octopus;

import java.util.concurrent.CompletionStage;

import com.example.step4.octopus.actor.prefixcall.PrefixCallActor;
import com.example.step4.octopus.actor.prefixcall.PrefixCallMessage;
import com.example.step4.octopus.http.Router;

import akka.actor.typed.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.AllDirectives;

public class App extends AllDirectives {

	public static void main(String[] args) throws Exception {

		// アクターシステムを作成
		final ActorSystem<PrefixCallMessage> system = ActorSystem.create(PrefixCallActor.create(), "octopus"); // PrefixCallのCRUDだけひとまず作るので、ルートのActorをプリフィクスコールにしたが、いろいろ要素がある場合にはさらに上位に抽象的なコマンド的なActorを作るべきなのかな。

		Http http = Http.get(system);
		Router router = new Router();

		CompletionStage<ServerBinding> binding = http.newServerAt("localhost", 8080).bind(router.createRoute(system));
		System.out.println("*** サーバーを起動しました。 http://localhost:8080 ***");
		System.out.println("*** 終了するにはEnterキーを押下してください ***");
		System.in.read();

		binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());

		// TODO
		// ひとまずActorの単位を、プリフィクスコールを操作するバッチ。的な粒度で作ったけど、プリフィクスコールのID１つにつき、１つのActorを作る感じの方が良いのだろうか？イベントソーシングにまだ触れていないので、そちらを勉強したらわかるかな？

		// TODO エラー発生するパターンをやる
		// TODO イベントソーシングする
	}
}
