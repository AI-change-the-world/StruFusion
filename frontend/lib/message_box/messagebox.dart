import 'package:flutter/material.dart';

abstract class MessageBox {
  String content;
  String stage;
  String think;

  MessageBox({required this.content, required this.stage, this.think = ""});

  Widget toWidget();
}
