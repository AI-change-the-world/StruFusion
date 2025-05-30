import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/base_response.dart';
import 'package:frontend/dialog_wrapper.dart';
import 'package:frontend/dio_client.dart';
import 'package:frontend/kb/kb_file_content_response.dart';
import 'package:frontend/logger.dart';
import 'package:frontend/styles.dart';
import 'package:markdown_widget/widget/markdown.dart';

class KbFileContentWidget extends StatefulWidget {
  const KbFileContentWidget({
    super.key,
    required this.content,
    required this.fileId,
  });
  final String content;
  final int fileId;

  @override
  State<KbFileContentWidget> createState() => _KbFileContentWidgetState();
}

class _KbFileContentWidgetState extends State<KbFileContentWidget>
    with SingleTickerProviderStateMixin {
  late final tabController = TabController(length: 2, vsync: this);

  @override
  void dispose() {
    tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return dialogWrapper(
      width: MediaQuery.of(context).size.width * 0.6,
      height: MediaQuery.of(context).size.height * 0.6,
      child: Container(
        padding: EdgeInsets.all(10),
        // child: MarkdownWidget(data: e.content ?? ""),
        child: Column(
          children: [
            SizedBox(
              height: 30,
              child: Row(
                children: [
                  SizedBox(
                    width: 250,
                    child: TabBar(
                      dividerHeight: 0,
                      padding: EdgeInsets.zero,
                      controller: tabController,
                      tabs: [Tab(text: "文件内容"), Tab(text: "文件元数据")],
                      // isScrollable: true,
                    ),
                  ),
                  Spacer(),
                ],
              ),
            ),
            Divider(height: 1, color: Colors.grey),
            Expanded(
              child: TabBarView(
                physics: const NeverScrollableScrollPhysics(),
                controller: tabController,
                children: [
                  MarkdownWidget(data: widget.content, selectable: false),
                  Consumer(
                    builder: (context, ref, _) {
                      final state = ref.watch(provider(widget.fileId));

                      return state.when(
                        data: (data) {
                          if (data == null ||
                              data.data == null ||
                              data.data!.isEmpty) {
                            return Center(child: Text("暂无数据"));
                          }

                          List<KbFileContent> objs = data.data!;

                          return ListView.builder(
                            itemBuilder: (context, index) {
                              var data = objs[index];
                              return Container(
                                constraints: BoxConstraints(minHeight: 30),
                                // height: 30,
                                child: Row(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Expanded(
                                      flex: 1,
                                      child: Text.rich(
                                        TextSpan(
                                          children: [
                                            TextSpan(
                                              text: "名称: ",
                                              style:
                                                  Styles.defaultButtonTextStyle,
                                            ),
                                            TextSpan(
                                              text: data.name,
                                              style:
                                                  Styles
                                                      .defaultButtonTextStyleGrey,
                                            ),
                                          ],
                                        ),
                                      ),
                                    ),
                                    // Expanded(child: Text("列名: ${data["alias"]}")),
                                    Expanded(
                                      flex: 3,
                                      child: Text.rich(
                                        TextSpan(
                                          children: [
                                            TextSpan(
                                              text: "内容: ",
                                              style:
                                                  Styles.defaultButtonTextStyle,
                                            ),
                                            TextSpan(
                                              text: data.content,
                                              style:
                                                  Styles
                                                      .defaultButtonTextStyleGrey,
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
                          );
                        },
                        error: (e, s) {
                          return Center(child: Text("Error: $e"));
                        },
                        loading:
                            () => const Center(
                              child: CircularProgressIndicator(),
                            ),
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

final provider = FutureProvider.family.autoDispose<KbFileContentResponse?, int>(
  (ref, id) async {
    try {
      final response = await DioClient().instance.get("/file/$id/content");
      BaseResponse<KbFileContentResponse> baseResponse = BaseResponse.fromJson(
        response.data,
        (json) => KbFileContentResponse.fromJson({"data": json}),
      );

      return baseResponse.data;
    } catch (e, s) {
      logger.e("Error fetching content: $e");
      logger.e("Stack trace: $s");
      return null;
    }
  },
);
