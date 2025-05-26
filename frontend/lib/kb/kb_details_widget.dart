import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/base_response.dart';
import 'package:frontend/dialog_wrapper.dart';
import 'package:frontend/dio_client.dart';
import 'package:frontend/kb/kb_file_response.dart';
import 'package:frontend/kb/kb_response.dart';
import 'package:frontend/logger.dart';
import 'package:frontend/styles.dart';
import 'package:he/he.dart' show AnimatedTile;
import 'package:markdown_widget/widget/all.dart';

class PageNotifier extends AutoDisposeNotifier<int> {
  final PageController controller = PageController(initialPage: 0);

  @override
  int build() {
    ref.onDispose(() {
      controller.dispose();
    });
    return 0;
  }

  changePage(int index) {
    if (state == index) {
      return;
    }
    state = index;
    controller.jumpToPage(index);
  }
}

final pageProvider = AutoDisposeNotifierProvider<PageNotifier, int>(
  PageNotifier.new,
);

class KbDetailsWidget extends ConsumerStatefulWidget {
  const KbDetailsWidget({super.key, required this.kb});
  final KB kb;

  @override
  ConsumerState<KbDetailsWidget> createState() => _KbDetailsWidgetState();
}

class _KbDetailsWidgetState extends ConsumerState<KbDetailsWidget> {
  @override
  Widget build(BuildContext context) {
    int index = ref.watch(pageProvider);

    return dialogWrapper(
      width: MediaQuery.of(context).size.width * 0.9,
      height: MediaQuery.of(context).size.height * 0.9,
      child: Container(
        padding: EdgeInsets.all(10),
        child: Column(
          children: [
            SizedBox(
              height: 30,
              child: Row(
                // spacing: 10,
                children: [
                  InkWell(
                    onTap: () {
                      ref.read(pageProvider.notifier).changePage(0);
                    },
                    child: Container(
                      padding: EdgeInsets.only(left: 10),
                      alignment: Alignment.centerLeft,
                      decoration: BoxDecoration(
                        color:
                            index == 0 ? Colors.lightBlueAccent : Colors.white,
                      ),
                      width: 100,
                      height: 30,
                      child: Row(
                        spacing: 5,
                        children: [
                          Icon(
                            Icons.details,
                            size: 18,
                            color: index == 0 ? Colors.white : Colors.black,
                          ),
                          Text(
                            "详情",
                            style: TextStyle(
                              color: index == 0 ? Colors.white : Colors.black,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                  // SizedBox(width: 10),
                  InkWell(
                    onTap: () {
                      ref.read(pageProvider.notifier).changePage(1);
                    },
                    child: Container(
                      padding: EdgeInsets.only(left: 10),
                      alignment: Alignment.centerLeft,
                      decoration: BoxDecoration(
                        color:
                            index == 1 ? Colors.lightBlueAccent : Colors.white,
                      ),
                      width: 100,
                      height: 30,
                      child: Row(
                        spacing: 5,
                        children: [
                          Icon(
                            Icons.file_copy,
                            size: 18,
                            color: index == 1 ? Colors.white : Colors.black,
                          ),
                          Text(
                            "文件列表",
                            style: TextStyle(
                              color: index == 1 ? Colors.white : Colors.black,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                  Spacer(),
                ],
              ),
            ),
            Divider(height: 0.5),
            Expanded(
              child: PageView(
                controller: ref.read(pageProvider.notifier).controller,
                children: [
                  _KBDetails(kb: widget.kb),
                  Consumer(
                    builder: (context, ref, child) {
                      final state = ref.watch(provider(widget.kb.id));

                      return state.when(
                        data: (d) {
                          if (d == null || d.files.isEmpty) {
                            return Center(child: Text("Empty"));
                          }
                          return Padding(
                            padding: const EdgeInsets.all(10.0),
                            child: Wrap(
                              spacing: 10,
                              runSpacing: 10,
                              children:
                                  d.files.map((e) {
                                    return AnimatedTile(
                                      width: 160,
                                      height: 100,
                                      color: const Color.fromARGB(
                                        255,
                                        89,
                                        241,
                                        255,
                                      ),
                                      title: e.name,
                                      icon: Icon(
                                        Icons.file_copy,
                                        color: Colors.white,
                                        size: 18,
                                      ),
                                      onTap: () {
                                        showGeneralDialog(
                                          barrierColor: Styles.barriarColor,
                                          barrierDismissible: true,
                                          barrierLabel: "file content",
                                          context: context,
                                          pageBuilder: (c, _, __) {
                                            return Center(
                                              child: dialogWrapper(
                                                width:
                                                    MediaQuery.of(
                                                      context,
                                                    ).size.width *
                                                    0.6,
                                                height:
                                                    MediaQuery.of(
                                                      context,
                                                    ).size.height *
                                                    0.6,
                                                child: Container(
                                                  padding: EdgeInsets.all(10),
                                                  child: MarkdownWidget(
                                                    data: e.content ?? "",
                                                  ),
                                                ),
                                              ),
                                            );
                                          },
                                        );
                                      },
                                    );
                                  }).toList(),
                            ),
                          );
                        },
                        error: (e, s) {
                          return Center(
                            child: Text("Error loading KB details."),
                          );
                        },
                        loading: () {
                          return Center(child: CircularProgressIndicator());
                        },
                      );
                    },
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _KBDetails extends StatelessWidget {
  const _KBDetails({required this.kb});
  final KB kb;

  @override
  Widget build(BuildContext context) {
    var objs = jsonDecode(kb.points ?? "[]");

    return Column(
      children: [
        SizedBox(
          height: 30,
          child: Row(
            children: [
              Expanded(
                flex: 1,
                child: Text("知识库名称：", style: Styles.defaultButtonTextStyle),
              ),
              Expanded(
                flex: 3,
                child: Text(
                  kb.name,
                  style: Styles.defaultButtonTextStyleNormal,
                ),
              ),
            ],
          ),
        ),
        SizedBox(
          height: 30,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 1,
                child: Text("知识库描述：", style: Styles.defaultButtonTextStyle),
              ),
              Expanded(
                flex: 3,
                child: Text(
                  kb.description,
                  style: Styles.defaultButtonTextStyleNormal,
                ),
              ),
            ],
          ),
        ),
        SizedBox(
          height: 30,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 1,
                child: Text("创建时间：", style: Styles.defaultButtonTextStyle),
              ),
              Expanded(
                flex: 3,
                child: Text(
                  kb.createdAt,
                  style: Styles.defaultButtonTextStyleNormal,
                ),
              ),
            ],
          ),
        ),
        SizedBox(
          height: 30,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 1,
                child: Text("修改时间：", style: Styles.defaultButtonTextStyle),
              ),
              Expanded(
                flex: 3,
                child: Text(
                  kb.createdAt,
                  style: Styles.defaultButtonTextStyleNormal,
                ),
              ),
            ],
          ),
        ),
        SizedBox(
          height: 30,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 1,
                child: Text("要点：", style: Styles.defaultButtonTextStyle),
              ),
            ],
          ),
        ),
        Expanded(
          child: Padding(
            padding: const EdgeInsets.only(left: 50),
            child: ListView.builder(
              itemBuilder: (context, index) {
                var data = (objs as List)[index];
                return SizedBox(
                  height: 30,
                  child: Row(
                    children: [
                      Expanded(
                        child: Text.rich(
                          TextSpan(
                            children: [
                              TextSpan(
                                text: "名称: ",
                                style: Styles.defaultButtonTextStyle,
                              ),
                              TextSpan(
                                text: data["name"],
                                style: Styles.defaultButtonTextStyleGrey,
                              ),
                            ],
                          ),
                        ),
                      ),
                      // Expanded(child: Text("列名: ${data["alias"]}")),
                      Expanded(
                        child: Text.rich(
                          TextSpan(
                            children: [
                              TextSpan(
                                text: "列名: ",
                                style: Styles.defaultButtonTextStyle,
                              ),
                              TextSpan(
                                text: data["alias"],
                                style: Styles.defaultButtonTextStyleGrey,
                              ),
                            ],
                          ),
                        ),
                      ),
                      Expanded(
                        child: Text.rich(
                          TextSpan(
                            children: [
                              TextSpan(
                                text: "类型: ",
                                style: Styles.defaultButtonTextStyle,
                              ),
                              TextSpan(
                                text: data["type"],
                                style: Styles.defaultButtonTextStyleGrey,
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                );
              },
              itemCount: objs.length,
            ),
          ),
        ),
      ],
    );
  }
}

final provider = FutureProvider.family.autoDispose<KbFileResponse?, int>((
  ref,
  id,
) async {
  try {
    final response = await DioClient().instance.get("/file/list/$id");
    BaseResponse<KbFileResponse> baseResponse = BaseResponse.fromJson(
      response.data,
      (json) => KbFileResponse.fromJson({"files": json}),
    );

    return baseResponse.data;
  } catch (e, s) {
    logger.e("Error fetching kb list: $e");
    logger.e("Stack trace: $s");
    return null;
  }
});
