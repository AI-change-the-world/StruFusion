import 'messagebox.dart';

class MessageState {
  List<MessageBox> messages;
  bool isLoading;

  MessageState({this.messages = const [], this.isLoading = false});

  MessageState copyWith({List<MessageBox>? messages, bool? isLoading}) {
    return MessageState(
      messages: messages ?? this.messages,
      isLoading: isLoading ?? this.isLoading,
    );
  }
}
