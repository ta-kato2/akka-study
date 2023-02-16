package com.example.step4.octopus.actor.prefixcall.message;

import com.example.step4.octopus.actor.prefixcall.PrefixCallMessage;

import akka.actor.typed.ActorRef;

public class ReadPrefixCallMessage implements PrefixCallMessage {

	public String id;

	public ActorRef<ReadPrefixCallMessage> callerRef;
}
