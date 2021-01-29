# LooperView
个人项目汇总

调用
String[] str = new String[]{"复方感冒灵片低至4.6元","20包板蓝根颗粒，速抢","同仁堂清喉利咽玩9.99","过年换新手机,低至1999","保险节能冰箱,低至729元"};
messages = new ArrayList<>(Arrays.asList(str));
looperShowView.startWithList(messages);

looperShowView.setOnItemClickListener(new LooperShowView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {
                ToastUtils.showLong(str[position]);
            }
        });
