package com.neo.sync;

import com.neo.entity.DateInterval;
import com.neo.entity.SyncFilePlan;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFilenameFilter;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FilenameFilter;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

 public  class FilesFilter implements FilenameFilter,FTPFileFilter,SmbFilenameFilter {

        private SyncFilePlan syncFilePlan;
        private String ftpFileParentDir;

        public FilesFilter(SyncFilePlan syncFilePlan){
            this.syncFilePlan=syncFilePlan;

        }

     public FilesFilter(SyncFilePlan syncFilePlan,String ftpFileParentDir) {
            this.syncFilePlan=syncFilePlan;
            this.ftpFileParentDir=ftpFileParentDir;

     }

     public Date getPastDate(int past){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
            Date today = calendar.getTime();
            return today;
        }
        public Set<Date> getDateList(){
            DateInterval dateInterval=syncFilePlan.getDateInterval();
            Set<Date> dateList = new HashSet<>();
            if(dateInterval==null){
                //没有设置，默认昨天和今天
                dateList.add(getPastDate(0));
                dateList.add(getPastDate(1));
                return dateList;
            }else {
                String singleDate=dateInterval.getSingleDate();
                String dateRange=dateInterval.getDateRange();

                if (!StringUtils.isEmpty(singleDate)) {
                    String dates[] = singleDate.split(",");
                    for (String dateStr : dates) {
                        dateList.add(getPastDate(Math.abs(Integer.parseInt(dateStr))));
                    }
                }
                if (!StringUtils.isEmpty(dateRange)) {
                    String range[] = dateRange.split(",");
                    Integer start = Integer.parseInt(range[0]);
                    Integer end = Integer.parseInt(range[1]);
                    for (int i = start; i <= end; i++) {
                        dateList.add(getPastDate(Math.abs(i)));
                    }
                }
                return dateList;
            }
        }
        private  boolean isSameDate(Date date1, Date date2) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);

            boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                    .get(Calendar.YEAR);
            boolean isSameMonth = isSameYear
                    && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
            boolean isSameDate = isSameMonth
                    && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                    .get(Calendar.DAY_OF_MONTH);

            return isSameDate;
        }
        private boolean nameFilter(String fileName ){
            String regexp=syncFilePlan.getNameFilter();
            if(StringUtils.isEmpty(regexp)) {
                return true;
            }
            String[] _regexp = regexp.split(",");
            String[] fbsArr = {  "$", "(", ")","+", ".", "[", "]", "^", "{", "}" };
            for (String reg : _regexp) {
                if(syncFilePlan.getIsRegex()==0) {
                    for(String str:fbsArr){
                        if(reg.contains(str)){
                            reg=reg.replace(str,"\\"+str);
                        }
                    }
                    reg = reg.replace("*", ".*").replace("?", ".");


                }
                //
                if(reg.contains("<")){
                    String format=reg.substring(reg.indexOf("<")+1,reg.indexOf(">"));
                    SimpleDateFormat sdf=new SimpleDateFormat(format);
                    Date date=getPastDate(0);
                    String dateStr=sdf.format(date);
                    if(fileName.contains(sdf.format(date))){
                    String newReg=reg.substring(0,reg.indexOf("<"))+dateStr+reg.substring(reg.indexOf(">")+1);
                    if(fileName.matches(newReg)){
                                    return true;
                                }
                            }

                }else {
                    Pattern pattern = Pattern.compile(reg);
                    if (pattern.matcher(fileName).matches()) {
                        return true;
                    }
                }
            }
            return false;
        }
        private int getAppearTimes(String source,String dest){
            int count = 0;
            int index = 0;
            while((index=dest.indexOf(source,index))!=-1){
                index = index+source.length();//根据在字符串中出现的位置，计数一次，下次从该位置后重新查找出现新的位置
                count++;
            }
            return count;
        }
        private boolean dateFilter(String filename,String parentDir,Long lastModify){
            boolean flag=false;

            String dirDateFormat=syncFilePlan.getDirFilterFormat();
            String dateFormats[]={"yyyyMMdd","yyyy-MM-dd","MMdd","MM-dd"};
            Set<Date> dateSet=getDateList();

            //需要验证名称日期
            if(syncFilePlan.getIsValidNameDate()==1){
                if(!StringUtils.isEmpty(dirDateFormat)){
                    dateFormats=dirDateFormat.split(",");
                }
                for(Date date:dateSet){
                    for(String format:dateFormats){
                        String formatS=format.replace("m","M").replace("D","d").replace("Y","y");
                        SimpleDateFormat sdf=new SimpleDateFormat(formatS);
                        String dateSdf=sdf.format(date);
                       if(getAppearTimes("m",format)==1){


                        if(parentDir.contains(dateSdf)||(!parentDir.contains(dateSdf)&&parentDir.replace("a","10").contains(dateSdf))||(!parentDir.contains(dateSdf)&&parentDir.replace("b","11").contains(dateSdf))||(!parentDir.contains(dateSdf)&&parentDir.replace("c","12").contains(dateSdf))){
                             flag=true;
                            break;
                          }
                           if(filename.contains(dateSdf)||(!filename.contains(dateSdf)&&filename.replace("a","10").contains(dateSdf))||(!filename.contains(dateSdf)&&filename.replace("b","11").contains(dateSdf))||(!filename.contains(dateSdf)&&filename.replace("c","12").contains(dateSdf))){
                               flag=true;
                               break;
                           }
                         }else if(getAppearTimes("M",format)==1){
                         if(parentDir.contains(dateSdf)||(!parentDir.contains(dateSdf)&&parentDir.replace("A","10").contains(dateSdf))||(!parentDir.contains(dateSdf)&&parentDir.replace("B","11").contains(dateSdf))||(!parentDir.contains(dateSdf)&&parentDir.replace("C","12").contains(dateSdf))){
                             flag=true;
                             break;
                        }
                           if(filename.contains(dateSdf)||(!filename.contains(dateSdf)&&filename.replace("A","10").contains(dateSdf))||(!filename.contains(dateSdf)&&filename.replace("B","11").contains(dateSdf))||(!filename.contains(dateSdf)&&filename.replace("C","12").contains(dateSdf))){
                               flag=true;
                               break;
                           }
                       }else if(parentDir.contains(dateSdf)||filename.contains(dateSdf)){
                           flag=true;
                           break;
                       }

                    }
                    if(flag){
                        break;
                    }
                }
            }else{
                flag=true;
            }
            if(!flag){
                return false;
            }
            if(syncFilePlan.getIsValidDate()==1){

                Date modify=new Date(lastModify);
                for(Date date:dateSet){
                    if(isSameDate(date,modify)){
                        return true;
                    }
                }
                return false;
            }else {
                return true;
            }
     }

        @Override
        public boolean accept(File dir, String name) {
            File file=new File(dir,name);
            if(file.isFile()) {

                return nameFilter(name) && dateFilter(name,dir.getName(),file.lastModified());
            }else{
                return true;
            }
        }

        public boolean accept(String source){

            File file=new File(source);
            if(file.isFile()) {

                return nameFilter(file.getName()) && dateFilter(file.getName(),file.getParentFile().getName(),file.lastModified());
            }else{
                return true;
            }
        }
        @Override
        public boolean accept(FTPFile file) {
            if(file.isFile()){
                return nameFilter(file.getName())&&dateFilter(file.getName(),ftpFileParentDir,file.getTimestamp().getTimeInMillis());
            }else{
               return true;
            }
        }

     @Override
     public boolean accept(SmbFile dir, String name) throws SmbException {
         SmbFile file= null;
         try {
             file = new SmbFile(dir,name);
         } catch (MalformedURLException e) {
             e.printStackTrace();
         } catch (UnknownHostException e) {
             e.printStackTrace();
         }
         if(file.isFile()) {
                 return nameFilter(name) && dateFilter(name,file.getParent(),file.getLastModified());
         }
         return true;

     }
 }



