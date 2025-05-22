class SseModel {
  final String? data;
  final String? message;
  final bool done;
  final String uuid;

  SseModel({
    required this.data,
    required this.message,
    this.done = false,
    required this.uuid,
  });

  factory SseModel.fromJson(Map<String, dynamic> json) {
    return SseModel(
      data: json['data'],
      message: json['message'] as String,
      done: json['done'] as bool,
      uuid: json['uuid'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {'data': data, 'message': message, 'done': done};
  }
}
