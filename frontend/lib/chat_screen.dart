import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/input_field.dart';
import 'package:frontend/logger.dart';
import 'package:frontend/message_box/chat_response.dart';
import 'package:frontend/message_box/controller.dart';
import 'package:frontend/message_box/llm_request_messagebox.dart';

import 'package:frontend/message_box/messagebox_state.dart';
import 'package:frontend/message_box/sse_model.dart';

import 'sse/sse.dart'; // for jsonDecode

typedef OnChat = Stream<ChatResponse> Function(String input);
typedef OnChatDone = void Function(Map<String, dynamic>? map);

class ChatUi extends ConsumerStatefulWidget {
  const ChatUi({super.key});

  @override
  ConsumerState<ChatUi> createState() => _ChatUiState();
}

class _ChatUiState extends ConsumerState<ChatUi> {
  final StreamController<String> _streamController =
      StreamController<String>.broadcast();

  late Stream<String> stream = _streamController.stream.asBroadcastStream();

  @override
  void initState() {
    super.initState();
    stream.listen((d) {
      logger.d("[data] $d");
      try {
        Map<String, dynamic> jsonData = jsonDecode(d);
        SseModel sseModel = SseModel.fromJson(jsonData);
        ChatResponse chatResponse = ChatResponse();
        // if (sseModel.done != true) {
        //   // chatResponse.content = sseModel.data ?? "";
        //   if (sseModel.data != null) {
        //     chatResponse.content = sseModel.data?.data ?? "";
        //     chatResponse.think = sseModel.data?.think ?? "";
        //   }
        //   chatResponse.uuid = sseModel.uuid;
        //   chatResponse.stage = sseModel.message ?? "回答中...";
        // } else {
        //   chatResponse.uuid = sseModel.uuid;
        //   chatResponse.stage = "done";
        //   chatResponse.done = true;
        // }

        if (sseModel.data != null) {
          chatResponse.content = sseModel.data?.data ?? "";
          chatResponse.think = sseModel.data?.think ?? "";
          print("think    ${sseModel.data?.think}");
        }
        chatResponse.uuid = sseModel.uuid;
        chatResponse.stage = sseModel.message ?? "回答中...";

        if (sseModel.done == true) {
          chatResponse.stage = "done";
          chatResponse.done = true;
        }

        ref.read(chatNotifierProvider.notifier).updateMessageBox(chatResponse);
      } catch (e, s) {
        logger.e("error $e \n stacktrace $s");
      }
    });
  }

  @override
  void dispose() {
    _streamController.close();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(chatNotifierProvider);

    return Column(
      children: [
        Flexible(
          child: SizedBox.expand(
            child: SingleChildScrollView(
              controller: ref.read(chatNotifierProvider.notifier).controller,
              padding: const EdgeInsets.only(left: 20, right: 20),
              child: Column(
                children: state.messages.map((e) => e.toWidget()).toList(),
              ),
            ),
          ),
        ),
        InputField(onSubmit: (s) => _handleInputMessage(s, state)),
      ],
    );
  }

  _handleInputMessage(String s, MessageState state) async {
    if (state.isLoading) {
      return;
    }
    final RequestMessageBox messageBox = RequestMessageBox(
      content: s,
      stage: "waiting...",
    );

    ref.read(chatNotifierProvider.notifier).addMessageBox(messageBox);

    sse("http://localhost:8080/strufusion/file/streamChat", {
      "kbId": 0,
      "message": s,
    }, _streamController);
  }
}
