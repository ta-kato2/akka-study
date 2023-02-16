package com.example.step4.octopus.actor.ncom;

import com.example.step4.octopus.TimeUtil;
import com.example.step4.octopus.actor.ncom.message.ApplyNcomMessage;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class NcomActor extends AbstractBehavior<NcomMessage> {

	public NcomActor(ActorContext<NcomMessage> context) {
		super(context);
	}

	/**
	 * ファクトリメソッド
	 */
	public static Behavior<NcomMessage> create() {
		return Behaviors.setup(NcomActor::new);
	}

	@Override
	public Receive<NcomMessage> createReceive() {
		return newReceiveBuilder().onMessage(ApplyNcomMessage.class, message -> {
			getContext().getLog().info("NCOMにプリフィクスコールを申込します。msidn={}", message.msisdn);

			// TODO NCOM-APIをCallして、申込する
			TimeUtil.sleep(5);

			return this;
		}).build();
	}
}
