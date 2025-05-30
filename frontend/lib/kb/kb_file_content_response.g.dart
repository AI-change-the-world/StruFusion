// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'kb_file_content_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

KbFileContentResponse _$KbFileContentResponseFromJson(
  Map<String, dynamic> json,
) => KbFileContentResponse(
  data:
      (json['data'] as List<dynamic>?)
          ?.map((e) => KbFileContent.fromJson(e as Map<String, dynamic>))
          .toList(),
);

Map<String, dynamic> _$KbFileContentResponseToJson(
  KbFileContentResponse instance,
) => <String, dynamic>{'data': instance.data};

KbFileContent _$KbFileContentFromJson(Map<String, dynamic> json) =>
    KbFileContent(
      id: (json['id'] as num).toInt(),
      kbId: (json['kbId'] as num).toInt(),
      kbFileId: (json['kbFileId'] as num).toInt(),
      name: json['name'] as String,
      alias: json['alias'] as String,
      content: json['content'] as String,
      updatedAt: json['updatedAt'] as String,
      createdAt: json['createdAt'] as String,
      contentType: json['contentType'] as String,
      reference: json['reference'] as String?,
    );

Map<String, dynamic> _$KbFileContentToJson(KbFileContent instance) =>
    <String, dynamic>{
      'id': instance.id,
      'kbId': instance.kbId,
      'kbFileId': instance.kbFileId,
      'name': instance.name,
      'alias': instance.alias,
      'content': instance.content,
      'updatedAt': instance.updatedAt,
      'createdAt': instance.createdAt,
      'contentType': instance.contentType,
      'reference': instance.reference,
    };
