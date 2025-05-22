import 'package:flutter/material.dart';

abstract class MessageBox {
  String content;
  String stage;

  MessageBox({required this.content, required this.stage});

  Widget toWidget();
}
