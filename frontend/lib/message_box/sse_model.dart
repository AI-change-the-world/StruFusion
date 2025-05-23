class SseModel {
  final DataWithThink? data;
  final String? message;
  final bool done;
  final String uuid;

  SseModel({this.data, this.message, this.done = false, required this.uuid});

  factory SseModel.fromJson(Map<String, dynamic> json) {
    return SseModel(
      data: json['data'] != null ? DataWithThink.fromJson(json['data']) : null,
      message: json['message'],
      done: json['done'],
      uuid: json['uuid'],
    );
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['data'] = this.data;
    data['message'] = message;
    data['done'] = done;
    data['uuid'] = uuid;
    return data;
  }
}

class DataWithThink {
  String? data;
  String? think;

  DataWithThink({this.data, this.think});

  factory DataWithThink.fromJson(Map<String, dynamic> json) {
    return DataWithThink(data: json['data'], think: json['think']);
  }

  Map<String, dynamic> toJson() {
    return {'data': data, 'think': think};
  }
}
