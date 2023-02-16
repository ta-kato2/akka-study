package com.example.step4.octopus.actor.prefixcall;

import com.example.step4.octopus.TimeUtil;
import com.example.step4.octopus.actor.ncom.NcomActor;
import com.example.step4.octopus.actor.ncom.NcomMessage;
import com.example.step4.octopus.actor.ncom.message.ApplyNcomMessage;
import com.example.step4.octopus.actor.prefixcall.message.CreatePrefixCallMessage;
import com.example.step4.octopus.actor.prefixcall.message.ReadPrefixCallMessage;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class PrefixCallActor extends AbstractBehavior<PrefixCallMessage> {

	private ActorRef<NcomMessage> ncomActorRef;

	public PrefixCallActor(ActorContext<PrefixCallMessage> context) {
		super(context);
		ncomActorRef = getContext().spawn(NcomActor.create(), "ncom");
	}

	/**
	 * ファクトリメソッド
	 */
	public static Behavior<PrefixCallMessage> create() {
		return Behaviors.setup(PrefixCallActor::new);
	}

	@Override
	public Receive<PrefixCallMessage> createReceive() {
		return newReceiveBuilder() //
				.onMessage(CreatePrefixCallMessage.class, this::createPrefixCall) //
				.onMessage(ReadPrefixCallMessage.class, this::readPrefixCall) //
				.build();
	}

	private Behavior<PrefixCallMessage> readPrefixCall(ReadPrefixCallMessage message) {
		getContext().getLog().info("プリフィクスコールを参照します。id={}", message.id);

		// TODO DBから？Akkaのイベントソースから？プリフィクスコールの情報を取得する。（
		// CQRSでやるなら、このユースケースはAkkaでは書かずに、別途Read用のAPI用意するだろうから、この処理いらないんだと思うけど）

		// 呼び出し元に情報を返却
		message.callerRef.tell(new ReadPrefixCallMessage()); // 受け取るメッセージと、同じ型で返す必要がある。なのでRESTのReadAPIのように情報取得するものには向いていない。処理が終わったよ。と通知する程度の使い方が正しいのだろうと想像。

		return this;
	}

	private PrefixCallActor createPrefixCall(CreatePrefixCallMessage message) {
		getContext().getLog().info("プリフィクスコールを作成します。msisdn={}", message.msisdn);

		// TODO DBにプリフィクスコールをInsertする
		TimeUtil.sleep(5);

		// NCOMへの申込イベントを発火
		ApplyNcomMessage applyMessage = new ApplyNcomMessage();
		applyMessage.msisdn = message.msisdn;
		ncomActorRef.tell(applyMessage);

		return this;
	}
}
