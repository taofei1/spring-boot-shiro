function parse(cronSplit,date){
	var ch="";	  
		//console.log(cronSplit +date);
	    if(cronSplit.indexOf(",")!=-1){
                ch="在"+cronSplit+date;
            }else if(cronSplit.indexOf("-")!=-1){
                var arr=cronSplit.split("-");
                ch="从"+arr[0]+"到"+arr[1]+date;
            }else if(cronSplit.indexOf("/")!=-1){
                var arr=cronSplit.split("/");
                  if(arr[0]=="*") {
                    ch += "每隔" + arr[1] + date;
                }else{
                	ch+="从"+arr[0]+date+"开始,每隔" + arr[1] + date;
				}
            }else {
				ch=cronSplit+date;
			}
			//console.log(ch);
			return ch;
}
function parseWeek(week){
	var ch="";
	switch(week){
		case '1':
			ch="星期日";
			break;
		case '2':
			ch="星期一";
			break;
		case '3':
			ch="星期二";
			break;
		case '4':
			ch="星期三";
			break;
		case '5':
			ch="星期四";
			break;
		case '6':
			ch="星期五";
			break;
		case '7':
			ch="星期六";
			break;
        case 'SUN':
            ch="星期日";
            break;
        case 'MON':
            ch="星期一";
            break;
        case 'TUE':
            ch="星期二";
            break;
        case 'WED':
            ch="星期三";
            break;
        case 'THU':
            ch="星期四";
            break;
        case 'FRI':
            ch="星期五";
            break;
        case 'SAT':
            ch="星期六";
            break;
	}
	return ch;
}
function cronParse(cronExpression){
    if(cronExpression==""){
        return "";
    }
    var list=cronExpression.split(" ");
    var ch="";
    if(list.length==7){
        if(list[6]!="") {
            ch += parse(list[6], "年");
        }
	}
        //解析月
        if(list[4]!="*")
        {
           ch=ch+parse(list[4],"月");
        }
        else
        {
			if(list.length==7||(list[3]!="*" &&list[3]!="?")||(list[5]!=="*" &&list[5]!="?")){
				ch+="每月";
			}
        }
		//解析周
          if(list[5]!=="*" &&list[5]!="?")
          {
				if(list[5].indexOf(",")!=-1){
					var arr=list[5].split(",");
					var week;
					for(var i in arr){
						week=parseWeek(arr[i])+",";
					}
					week=week.substr(0,week.length-1);
					ch+="在"+week;
				}else if(list[5].indexOf("-")!=-1){
                    var arr=list[5].split("-");
                    ch+="从"+parseWeek(arr[0])+"到"+parseWeek(arr[1]);
                }else if(list[5].indexOf("/")!=-1){
                    var arr=list[5].split("/");
                    ch+="从"+parseWeek(arr[0])+"开始,每隔"+arr[1]+"周";
                }else if(list[5].indexOf("L")!=-1){
					var a=list[5].substr(0,list[5].length-1);
					//console.log(a);
					//console.log(parseWeek(a));
					ch=ch+"在最后一个"+parseWeek(a);
				}else if(list[5].indexOf("#")!=-1){
					var a=list[5].split("#");
					ch+="在第"+a[1]+"个"+parseWeek(a[0]);
				}else {
					ch+=parseWeek(list[5]);
				}
           
           }
        //解析日
        if(list[3]!="?")
        {
            if(list[3]!="*")
            {
                
                if(list[3].indexOf("L")!=-1&&list[3].indexOf("W")==-1){
                	ch+="月最后一天";
				}else if(list[3].indexOf("L")!=-1&&list[3].indexOf("W")!=-1){
                	ch+="月最后一个星期五";
				}else if(list[3].indexOf("L")==-1&&list[3].indexOf("W")!=-1){
                	var a=list[3].substr(0,list[3].length-1);
                	ch+="在"+a[1]+"号最近的工作日(周一-周五)";
				}else{
					ch+=parse(list[3],"号");
				}
            }
            else
            {
                ch+="每日";
            }
        }else{
			  if(list[5]=="*" ||list[5]=="?"){
				ch+="每日";
			  }
		}

        //解析时
        if(list[2]!="*")
        {
            ch+=parse(list[2],"时");
        }
        else
        {
           ch+="每时";
        }

        //解析分
        if(list[1]!="*")
        {
            ch+=parse(list[1],"分");
        }
        else
        {
            ch+="每分";
        }

        //解析秒
        if(list[0]!="*")
        {
            ch+=parse(list[0],"秒");
        }
        else
        {
            ch+="每秒";
        }
        return ch;

}