/*
{
      "id": 182,
      "kbId": 1,
      "kbFileId": 8,
      "name": "候选人姓名",
      "alias": "name",
      "content": "张伟",
      "updatedAt": "2025-05-26T01:06:39",
      "createdAt": "2025-05-26T01:06:39",
      "contentType": "str",
      "reference": null
    }
*/

import 'package:json_annotation/json_annotation.dart';

part 'kb_file_content_response.g.dart';

@JsonSerializable()
class KbFileContentResponse {
  final List<KbFileContent>? data;

  KbFileContentResponse({this.data});

  factory KbFileContentResponse.fromJson(Map<String, dynamic> json) =>
      _$KbFileContentResponseFromJson(json);

  Map<String, dynamic> toJson() => _$KbFileContentResponseToJson(this);
}

@JsonSerializable()
class KbFileContent {
  int id;
  int kbId;
  int kbFileId;
  String name;
  String alias;
  String content;
  String updatedAt;
  String createdAt;
  String contentType;
  String? reference;

  KbFileContent({
    required this.id,
    required this.kbId,
    required this.kbFileId,
    required this.name,
    required this.alias,
    required this.content,
    required this.updatedAt,
    required this.createdAt,
    required this.contentType,
    this.reference,
  });

  factory KbFileContent.fromJson(Map<String, dynamic> json) =>
      _$KbFileContentFromJson(json);

  Map<String, dynamic> toJson() => _$KbFileContentToJson(this);
}
