import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/base_response.dart';
import 'package:frontend/dio_client.dart';
import 'package:frontend/kb/card.dart';
import 'package:frontend/kb/kb_response.dart';
import 'package:frontend/logger.dart';

class KbScreen extends StatefulWidget {
  const KbScreen({super.key});

  @override
  State<KbScreen> createState() => _KbScreenState();
}

class _KbScreenState extends State<KbScreen> {
  @override
  Widget build(BuildContext context) {
    return Consumer(
      builder: (context, ref, child) {
        final state = ref.watch(provider);
        return state.when(
          data: (d) {
            return Padding(
              padding: const EdgeInsets.all(10.0),
              child: Wrap(
                runSpacing: 10,
                spacing: 10,
                children: (d?.kbs ?? []).map((e) => KBCard(kb: e)).toList(),
              ),
            );
          },
          error: (e, _) => Center(child: Text("$e")),
          loading: () => Center(child: CircularProgressIndicator()),
        );
      },
    );
  }
}

final provider = FutureProvider.autoDispose<KbResponse?>((ref) async {
  try {
    final response = await DioClient().instance.get("/kb/list");
    BaseResponse<KbResponse> baseResponse = BaseResponse.fromJson(
      response.data,
      (json) => KbResponse.fromJson({"kbs": json}),
    );

    return baseResponse.data;
  } catch (e, s) {
    logger.e("Error fetching kb list: $e");
    logger.e("Stack trace: $s");
    return null;
  }
});
