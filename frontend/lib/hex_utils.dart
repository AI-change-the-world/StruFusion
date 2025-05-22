import 'dart:ui';

class HexColor extends Color {
  static int _getColorFromHex(String hexColor) {
    hexColor = hexColor.toUpperCase().replaceAll("#", "");
    if (hexColor.length == 6) {
      hexColor = "FF$hexColor";
    }
    return int.parse(hexColor, radix: 16);
  }

  HexColor(final String hexColor) : super(_getColorFromHex(hexColor));
}

class Styles {
  Styles._();

  static Color black515A6E = HexColor('#515A6E');
  static Color divider = HexColor('#DCDFE6');
  static Color blue247AF2 = HexColor('#247AF2');
}
