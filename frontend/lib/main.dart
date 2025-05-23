import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/app/route.dart';
import 'package:frontend/dio_client.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  DioClient().init(baseUrl: "http://127.0.0.1:8080/strufusion");
  runApp(const ProviderScope(child: MyApp()));
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color.fromARGB(255, 61, 124, 219),
          brightness: Brightness.light,
        ),
      ),
      // showPerformanceOverlay: true,
      debugShowCheckedModeBanner: false,
      routerConfig: router,
    );
  }
}
