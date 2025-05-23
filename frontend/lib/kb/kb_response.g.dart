// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'kb_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

KbResponse _$KbResponseFromJson(Map<String, dynamic> json) => KbResponse(
  kbs:
      (json['kbs'] as List<dynamic>)
          .map((e) => KB.fromJson(e as Map<String, dynamic>))
          .toList(),
);

Map<String, dynamic> _$KbResponseToJson(KbResponse instance) =>
    <String, dynamic>{'kbs': instance.kbs};

KB _$KBFromJson(Map<String, dynamic> json) => KB(
  id: (json['id'] as num).toInt(),
  name: json['name'] as String,
  description: json['description'] as String,
  updatedAt: json['updatedAt'] as String,
  createdAt: json['createdAt'] as String,
  points: json['points'] as String?,
);

Map<String, dynamic> _$KBToJson(KB instance) => <String, dynamic>{
  'id': instance.id,
  'name': instance.name,
  'description': instance.description,
  'updatedAt': instance.updatedAt,
  'createdAt': instance.createdAt,
  'points': instance.points,
};
