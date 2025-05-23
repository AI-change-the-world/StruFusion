import 'package:json_annotation/json_annotation.dart';

part 'kb_file_response.g.dart';

@JsonSerializable()
class KbFileResponse {
  List<KbFile> files;

  KbFileResponse({required this.files});

  factory KbFileResponse.fromJson(Map<String, dynamic> json) =>
      _$KbFileResponseFromJson(json);

  Map<String, dynamic> toJson() => _$KbFileResponseToJson(this);
}

@JsonSerializable()
class KbFile {
  int id;
  String name;
  String updatedAt;
  String createdAt;
  String? content;

  KbFile({
    required this.id,
    required this.name,
    required this.updatedAt,
    required this.createdAt,
    required this.content,
  });

  factory KbFile.fromJson(Map<String, dynamic> json) => _$KbFileFromJson(json);

  Map<String, dynamic> toJson() => _$KbFileToJson(this);
}
