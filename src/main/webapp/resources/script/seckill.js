//javascript模块化
var seckill = {
    //封装相关ajax的url
    URL: {
        now : function () {
           return "/seckill/time/now";
        },
        exposer: function (seckillId) {
            return "/seckill/"+seckillId+"/exposer";
        },
        execution: function (seckillId, md5) {
            return "/seckill/"+seckillId+"/"+md5+"/execution";
        }
    },
    validatePhone : function (phone) {
        if(phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    handleSeckillKill: function (seckillId, node) {
        //暴露秒杀接口
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            if(result && result['success']) {
                var exposerResult = result['data'];
                var md5 = exposerResult['md5'];
                if(exposerResult['exposed']) {
                    //开启秒杀
                    node.hide().html('<label class="btn btn-primary" id="seckillBtn">开始秒杀</label>');
                    var seckillBtn = $('#seckillBtn');
                    seckillBtn.one('click', function () {
                        $(this).addClass('disabled');
                        seckill.executeSeckillKill(seckillId, md5, node);
                    });
                    node.show();
                } else {
                    var now = exposerResult['now'];
                    var start = exposerResult['start'];
                    var end = exposerResult['end'];
                    //未开启或客户端时间与服务器时间不同步
                    seckill.countdown(seckillId, now, start, end);
                }
            } else {
                console.log(result);
            }
        });
    },
    //提交秒杀
    executeSeckillKill : function (seckillId, md5, node) {
        $.post(seckill.URL.execution(seckillId, md5), {}, function (result) {
            if(result && result['success']) {
                var executionResult = result['data'];
                var stateInfo = executionResult['stateInfo'];
                node.hide().html('<label class="label label-success">' + stateInfo + '</label>').show();
            } else {
                console.log(result);
            }
        });
    },
    //计时
    countdown:function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        if(nowTime > endTime) {
            seckillBox.html("秒杀结束!");
        } else if(nowTime < startTime) {
            //秒杀未开始，显示倒计时
            //加一秒防止用户时间偏移
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
               var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finished.countdown', function () {
                seckill.handleSeckillKill(seckillId, seckillBox);
            });
        } else {
            //秒杀开始，获取秒杀url
            seckill.handleSeckillKill(seckillId, seckillBox);
        }
    },
    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init : function (params) {
            //手机验证和登录，计时交互
            var killphone = $.cookie('killPhone');
            //未登录
            if(!seckill.validatePhone(killphone)) {
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show : true, //显示弹出层
                    backdrop : 'static', //禁止位置关闭
                    keyboard: false //禁用键盘事件
                });
                $('#killphoneBtn').click(function () {
                    var inputPhone = $('#killphoneKey').val();
                    console.log(inputPhone);
                    if(seckill.validatePhone(inputPhone)) {
                        $.cookie('killPhone', inputPhone, {expires:7, path:'/seckill'});
                        window.location.reload();
                    } else {
                        $('#killphoneMessage').hide().html('<label class="label label-danger">手机号输入有误！</label>').show(300);
                    }
                });
            }
            //已经登录
            var seckillId = params['seckillId'];
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            //获取系统时间
            $.get(seckill.URL.now(), {}, function (result) {
                if(result && result['success']) {
                    var nowTime = result['data'];
                    //时间判断
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log(result);
                }
            },"json");
        }
    }
}