import 'package:json_annotation/json_annotation.dart';

part 'kb_response.g.dart';

@JsonSerializable()
class KbResponse {
  List<KB> kbs;

  KbResponse({required this.kbs});

  factory KbResponse.fromJson(Map<String, dynamic> json) =>
      _$KbResponseFromJson(json);

  Map<String, dynamic> toJson() => _$KbResponseToJson(this);
}

@JsonSerializable()
class KB {
  int id;
  String name;
  String description;
  String updatedAt;
  String createdAt;
  String? points;

  KB({
    required this.id,
    required this.name,
    required this.description,
    required this.updatedAt,
    required this.createdAt,
    this.points,
  });

  factory KB.fromJson(Map<String, dynamic> json) => _$KBFromJson(json);

  Map<String, dynamic> toJson() => _$KBToJson(this);
}
