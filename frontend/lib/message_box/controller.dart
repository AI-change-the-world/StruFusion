import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/message_box/chat_response.dart';
import 'package:frontend/message_box/llm_response_messagebox.dart';
import 'package:frontend/message_box/messagebox.dart';
import 'package:frontend/message_box/messagebox_state.dart';

class ChatNotifier extends AutoDisposeNotifier<MessageState> {
  MessageBox? getByUuid(String uuid) {
    return state.messages
        .where((element) => element is ResponseMessageBox && element.id == uuid)
        .firstOrNull;
  }

  final ScrollController controller = ScrollController();

  MessageBox? getLastMessage() {
    return state.messages.lastOrNull;
  }

  void setLoading(bool b) async {
    if (b == state.isLoading) return;
    state = state.copyWith(isLoading: b);
  }

  void addMessageBox(MessageBox box) async {
    if (state.isLoading) return;
    state = state.copyWith(messages: [...state.messages, box]);
  }

  void updateLastMessage(String content) {
    final box = getLastMessage();

    if (box != null && box is ResponseMessageBox) {
      final l = List<MessageBox>.from(state.messages)..remove(box);
      box.content = content;
      state = state.copyWith(messages: [...l, box]);
    }
  }

  void updateMessageBox(ChatResponse response) {
    final box =
        state.messages
            .where(
              (element) =>
                  element is ResponseMessageBox && element.id == response.uuid,
            )
            .firstOrNull;

    if (box != null) {
      final l = List<MessageBox>.from(state.messages)..remove(box);
      box.content += response.content ?? "";
      box.think += response.think;
      if (box is ResponseMessageBox) {
        box.stage = response.stage ?? "";
      }
      state = state.copyWith(
        messages: [...l, box],
        isLoading: !(response.done ?? false),
      );
      controller.jumpTo(controller.position.maxScrollExtent);
    } else {
      final l = List<MessageBox>.from(state.messages)..add(
        ResponseMessageBox(
          content: response.content ?? "",
          id: response.uuid!,
          stage: response.stage ?? "",
          think: response.think,
        ),
      );

      state = state.copyWith(messages: l, isLoading: !(response.done ?? false));
      controller.jumpTo(controller.position.maxScrollExtent);
    }
  }

  @override
  MessageState build() {
    ref.onDispose(() {
      controller.dispose();
    });

    return MessageState(messages: [], isLoading: false);
  }
}

final chatNotifierProvider =
    AutoDisposeNotifierProvider<ChatNotifier, MessageState>(ChatNotifier.new);
