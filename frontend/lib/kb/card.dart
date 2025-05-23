import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/kb/kb_details_widget.dart';
import 'package:frontend/kb/kb_response.dart';
import 'package:frontend/styles.dart';

class _CardState {
  final double xRotation;
  final double yRotation;
  final bool showIcon;

  const _CardState({
    this.xRotation = 0,
    this.yRotation = 0,
    this.showIcon = false,
  });

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is _CardState &&
        other.xRotation == xRotation &&
        other.yRotation == yRotation &&
        other.showIcon == showIcon;
  }

  @override
  int get hashCode =>
      xRotation.hashCode ^ yRotation.hashCode ^ showIcon.hashCode;

  _CardState copyWith({double? xRotation, double? yRotation, bool? showIcon}) {
    return _CardState(
      xRotation: xRotation ?? this.xRotation,
      yRotation: yRotation ?? this.yRotation,
      showIcon: showIcon ?? this.showIcon,
    );
  }
}

class KBCard extends ConsumerStatefulWidget {
  const KBCard({super.key, required this.kb});
  final KB kb;

  @override
  ConsumerState<KBCard> createState() => _KBCardState();
}

class _KBCardState extends ConsumerState<KBCard> {
  late ValueNotifier<_CardState> _cardState = ValueNotifier(_CardState());
  final double maxAngle = 0.15; // 最大旋转角度（弧度）

  @override
  void initState() {
    super.initState();
    _cardState = ValueNotifier(_CardState());
  }

  @override
  void dispose() {
    _cardState.dispose();
    super.dispose();
  }

  void _updateRotation(Offset position, Size size) {
    double dx = (position.dx - size.width / 2) / (size.width / 2); // -1 ~ 1
    double dy = (position.dy - size.height / 2) / (size.height / 2); // -1 ~ 1

    // 计算旋转角度，映射到 [-maxAngle, maxAngle]
    double newXRotation = dy * -maxAngle;
    double newYRotation = dx * maxAngle;
    _CardState newState = _cardState.value.copyWith(
      xRotation: newXRotation, // 控制前后倾斜
      yRotation: newYRotation, // 控制左右倾斜
    );
    if (_cardState.value != newState) {
      _cardState.value = newState;
    }
  }

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder(
      valueListenable: _cardState,
      builder: (c, s, _) {
        return MouseRegion(
          cursor: SystemMouseCursors.click,
          onEnter: (event) {
            _cardState.value = _CardState(showIcon: true);
          },
          onHover: (event) {
            final RenderBox box = context.findRenderObject() as RenderBox;
            final Offset localPosition = box.globalToLocal(event.position);
            _updateRotation(localPosition, box.size);
          },
          onExit: (_) => _cardState.value = _CardState(),
          child: Transform(
            alignment: Alignment.center,
            transform:
                Matrix4.identity()
                  ..setEntry(3, 2, 0.0015) // 透视效果
                  ..rotateX(s.xRotation)
                  ..rotateY(s.yRotation),
            child: GestureDetector(
              onTap: () {
                // ref
                //     .read(datasetNotifierProvider.notifier)
                //     .changeCurrent(widget.dataset);
                // GlobalDrawer.showDrawer();
                showGeneralDialog(
                  barrierColor: Styles.barriarColor,
                  barrierDismissible: true,
                  barrierLabel: "KbDetailsWidget",
                  context: context,
                  pageBuilder: (c, _, __) {
                    return Center(child: KbDetailsWidget(kb: widget.kb));
                  },
                );
              },
              onDoubleTap: () async {},
              child: _child(s),
            ),
          ),
        );
      },
    );
  }

  double iconSize = 18;
  Color iconColor = Colors.white;

  Widget _child(_CardState s) {
    return Container(
      width: 200,
      height: 150,
      decoration: BoxDecoration(
        color: Colors.lightBlueAccent,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.2),
            blurRadius: 10,
            spreadRadius: 2,
            offset: Offset(-s.yRotation * 20, s.xRotation * 20),
          ),
        ],
      ),

      child: Stack(
        children: [
          AnimatedAlign(
            duration: Duration(milliseconds: 300),
            alignment: s.showIcon ? Alignment.topLeft : Alignment.center,
            child: Padding(
              padding: EdgeInsets.all(10),
              child: Text(
                widget.kb.name.isNotEmpty ? widget.kb.name : "unknow",
                style: TextStyle(fontSize: 20, color: Colors.white),
                maxLines: 1,
                softWrap: true,
                overflow: TextOverflow.ellipsis,
              ),
            ),
          ),

          Positioned(
            left: 10,
            top: 40,
            child: AnimatedOpacity(
              opacity: s.showIcon ? 1 : 0,
              duration: Duration(milliseconds: 300),
              child: SizedBox(
                width: 180,
                child: Text(
                  widget.kb.description,
                  style: TextStyle(fontSize: 12, color: Colors.white70),
                  maxLines: 4,
                  softWrap: true,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ),
          ),
          if (s.showIcon)
            Positioned(
              bottom: 10,
              right: 10,
              child: Icon(Icons.library_books, color: Colors.white, size: 18),
            ),
        ],
      ),
    );
  }
}
