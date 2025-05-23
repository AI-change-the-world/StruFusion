class ChatResponse {
  String? stage;
  String? content;
  String? uuid;
  bool? done;
  String think;

  ChatResponse({
    this.stage,
    this.content,
    this.uuid,
    this.done,
    this.think = '',
  });

  // 从 Map 转换为 ChatResponse 对象
  factory ChatResponse.fromJson(Map<String, dynamic> json) {
    return ChatResponse(
      stage: json['stage'] as String?,
      content: json['content'] as String?,
      uuid: json['uuid'] as String?,
      done: json['done'] as bool?,
    );
  }

  // 将 ChatResponse 对象转换为 Map
  Map<String, dynamic> toJson() {
    return {'stage': stage, 'content': content, 'uuid': uuid, 'done': done};
  }

  @override
  String toString() {
    return 'ChatResponse(stage: $stage, content: $content, uuid: $uuid, done: $done)';
  }
}
