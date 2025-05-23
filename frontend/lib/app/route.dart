import 'package:flutter/material.dart';
import 'package:frontend/app/_simple_layout.dart';
import 'package:frontend/chat_screen.dart';
import 'package:frontend/kb/kb_screen.dart';
import 'package:frontend/styles.dart';
import 'package:go_router/go_router.dart';

final GoRouter router = GoRouter(
  errorPageBuilder: (context, state) {
    return MaterialPage<void>(
      key: state.pageKey,
      child: Scaffold(
        body: Center(
          child: Column(
            spacing: 20,
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text("Nothing here", style: const TextStyle(fontSize: 24)),
              ElevatedButton(
                style: Styles.getDefaultButtonStyle(width: 200),
                onPressed: () {
                  context.go("/");
                },
                child: Text("回到首页", style: Styles.defaultButtonTextStyle),
              ),
            ],
          ),
        ),
      ),
    );
  },
  initialLocation: '/',
  routes: [
    ShellRoute(
      builder: (context, state, child) => SimpleLayoutShell(child: child),
      routes: [
        GoRoute(
          path: '/',
          name: '知识库',
          pageBuilder: (context, state) => noTransitionPage(child: KbScreen()),
        ),
        // GoRoute(
        //   path: '/annotation',
        //   name: 'annotation',
        //   pageBuilder:
        //       (context, state) => noTransitionPage(child: AnnotationScreen()),
        // ),
        GoRoute(
          path: '/chat',
          name: '对话',
          // builder: (context, state) => const ModelScreen(),
          pageBuilder: (context, state) => noTransitionPage(child: ChatUi()),
        ),
      ],
    ),
  ],
);

CustomTransitionPage<void> noTransitionPage({required Widget child}) {
  return CustomTransitionPage<void>(
    child: child,
    transitionDuration: Duration.zero,
    transitionsBuilder: (context, animation, secondaryAnimation, child) {
      return child; // 直接返回 child，没有任何动画
    },
  );
}
