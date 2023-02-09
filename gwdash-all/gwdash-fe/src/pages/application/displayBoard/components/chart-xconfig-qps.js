/*
 * Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

let config = (function () {
  return function (that, myFullShow) {
    return {
      tooltip: {
        trigger: 'axis',
        axisPointer: { // 坐标轴指示器，坐标轴触发有效
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
      },
      color: ['#88bf4d'],
      title: [{
        text: '今天Qps最多项目Top10',
        // subtext: '总计 ' + builderJson.all,
        left: '12%',
        top: '5%',
        textAlign: 'center',
        textStyle: {
          // 文字颜色
          color: 'black',
          // 字体风格,'normal','italic','oblique'
          // fontStyle:'normal',
          // //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
          // fontWeight:'bold',
          // //字体系列
          // fontFamily:'sans-serif',
          // 字体大小
          fontSize: 16
        }
      }],
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        name: '元',
        type: 'value',
        splitLine: { show: false }, // 去除网格线
        axisLine: {
          lineStyle: {
            color: '#aeb4b7fa'
          },
          width: 1
        },
        axisLabel: {
          textStyle: {
            color: 'black'
          }
        }
      },
      yAxis: {
        type: 'category',
        data: that.mostQps.filter(it => it.qps !== 0).map(e => e.projectName).reverse(),
        axisLabel: {
          color: "white",
          inside: true,
          fontSize: 14,
          margin: 20
        },
        splitLine: { show: false }, // 去除网格线
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        z: 10
      },
      series: [
        {
          type: 'bar',
          stack: '总量',
          label: {
            show: true,
            position: 'insideRight'
          },
          data: that.mostQps.filter(it => it.qps !== 0).map((e) => {
            return Number(e.qps)
          }).reverse()
        }
      ]
    }
  }
})()

export default config
