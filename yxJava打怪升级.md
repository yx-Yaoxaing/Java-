# Java-
包com.yx.array;
#
导入java.util.Arrays;

/**
 * @author yaoxiang
 * @date 2021-01-15 14:39
 */
public class ArraySort_CodeTest {
    public static void main(String[] args) {
        int arr[] = {1,2,3,4,5,6,7,8};
        System.out.println(ArraysSort(arr,5));
    }
    public static int ArraysSort(int[] arr , int value){
        Arrays.sort(arr);
        //二分查找
        /**
         * 二分查找前提条件是 数组中的数据必须排序
         * */
        //定义取出数组的中间长度
        int lowerBound=0;
        int upperBound=arr.length-1;
        int curIn=0;
       while (true){
           curIn=(lowerBound+upperBound)/2;
           //首先判断 需要查询的值是否就是数组中间的值
           /**
            * 如果需要查询的值 大于中间的值 就去左边找 如果小于 就去右边找
            * */
          if(arr[curIn]==value){
              return curIn;
          }else if(lowerBound>upperBound){
              return arr.length;
           }else{
              if(arr[curIn]<value){
                   lowerBound=curIn+1;
              } else if(arr[curIn]>value){
                  upperBound=curIn-1;
              }else{
                  return  curIn;
              }

          }

       }

    }
}
