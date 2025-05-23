// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'kb_file_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

KbFileResponse _$KbFileResponseFromJson(Map<String, dynamic> json) =>
    KbFileResponse(
      files:
          (json['files'] as List<dynamic>)
              .map((e) => KbFile.fromJson(e as Map<String, dynamic>))
              .toList(),
    );

Map<String, dynamic> _$KbFileResponseToJson(KbFileResponse instance) =>
    <String, dynamic>{'files': instance.files};

KbFile _$KbFileFromJson(Map<String, dynamic> json) => KbFile(
  id: (json['id'] as num).toInt(),
  name: json['name'] as String,
  updatedAt: json['updatedAt'] as String,
  createdAt: json['createdAt'] as String,
  content: json['content'] as String?,
);

Map<String, dynamic> _$KbFileToJson(KbFile instance) => <String, dynamic>{
  'id': instance.id,
  'name': instance.name,
  'updatedAt': instance.updatedAt,
  'createdAt': instance.createdAt,
  'content': instance.content,
};
