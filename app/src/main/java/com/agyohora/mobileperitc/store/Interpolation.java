package com.agyohora.mobileperitc.store;

import android.util.Log;

import com.agyohora.mobileperitc.utils.CommonUtils;

import java.util.Arrays;

/**
 * Created by Invent on 8-3-18.
 */

@SuppressWarnings("AccessStaticViaInstance")
public class Interpolation {

    public double[][] getsourceQuad(PerimetryObject_V2.TestMeasurementsInfo testMeasurementsInfo, int quadNo, String testPattern) {
        double[][] sourcequad = new double[][]{{1000, 1000, 1000, 1000, 1000}, {1000, 1000, 1000, 1000, 1000}, {1000, 1000, 1000, 1000, 1000}, {1000, 1000, 1000, 1000, 1000}, {1000, 1000, 1000, 1000, 1000}};
        for (int i = 0; i < testMeasurementsInfo.VisualFieldTestPointSequence.length; i++) {
            //loops across the length of the visual field test point sequence
            //choose which elements to process
            boolean b = testPattern.equals("24-2") || testPattern.equals("30-2");
            switch (quadNo) {
                case 1:
                    if (testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate > 0 &&
                            testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate > 0) {
                        if (b) {
                            int xval = (int) (((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate) - 3.0) / 6);
                            int yval = (int) (((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate) - 3.0) / 6);
                            sourcequad[xval][yval] = testMeasurementsInfo.VisualFieldTestPointSequence[i].SensitivityValue;
                        } else {
                            int xval = (int) (((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate) - 1.0) / 2);
                            int yval = (int) (((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate) - 1.0) / 2);
                            sourcequad[xval][yval] = testMeasurementsInfo.VisualFieldTestPointSequence[i].SensitivityValue;
                        }
                    }
                    break;
                case 2:
                    if (testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate < 0 &&
                            testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate > 0) {
                        if (b) {
                            int xval = (int) ((((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate) * -1) - 3.0) / 6);
                            int yval = (int) ((((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate) * 1) - 3.0) / 6);
                            //Log.e("Interpolation"," "+ xval+" "+yval);
                            sourcequad[xval][yval] = testMeasurementsInfo.VisualFieldTestPointSequence[i].SensitivityValue;
                        } else {
                            int xval = (int) ((((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate) * -1) - 1.0) / 2);
                            int yval = (int) ((((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate) * 1) - 1.0) / 2);
                            sourcequad[xval][yval] = testMeasurementsInfo.VisualFieldTestPointSequence[i].SensitivityValue;
                        }
                    }
                    break;
                case 3:
                    if (testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate < 0 &&
                            testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate < 0) {
                        if (b) {
                            int xval = (int) ((((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate) * -1) - 3.0) / 6);
                            int yval = (int) ((((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate) * -1) - 3.0) / 6);
                            sourcequad[xval][yval] = testMeasurementsInfo.VisualFieldTestPointSequence[i].SensitivityValue;
                        } else {
                            int xval = (int) ((((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate) * -1) - 1.0) / 2);
                            int yval = (int) ((((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate) * -1) - 1.0) / 2);
                            sourcequad[xval][yval] = testMeasurementsInfo.VisualFieldTestPointSequence[i].SensitivityValue;
                        }
                    }
                    break;
                case 4:
                    if (testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate > 0 &&
                            testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate < 0) {
                        if (b) {
                            int xval = (int) ((((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate) * 1) - 3.0) / 6);
                            int yval = (int) ((((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate) * -1) - 3.0) / 6);
                            sourcequad[xval][yval] = testMeasurementsInfo.VisualFieldTestPointSequence[i].SensitivityValue;
                        } else {
                            int xval = (int) ((((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate) * 1) - 1.0) / 2);
                            int yval = (int) ((((testMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate) * -1) - 1.0) / 2);
                            sourcequad[xval][yval] = testMeasurementsInfo.VisualFieldTestPointSequence[i].SensitivityValue;
                        }
                    }
                    break;
                default:
                    Log.e("Interpolation", "The quad switch case did not work");
                    break;
            }
        }
        return sourcequad;
    }

    public double[][] getBilenar(double[][] source) {
        //double[][] source = new double[][]{{0, 0, 0, 26,28}, {0, 4, 2, 28, 28}, {0, 12, 24, 27, 1000}, {15, 8, 15, 1000, 1000},{17,10,1000,1000,1000}};
        double[][] result = new double[13][13];
        BilinearInterpolator bi = new BilinearInterpolator();
        for (int i = 0; i < 13; i++) {
            double idx = 0 + i * (4.0 / 13.0);
            for (int j = 0; j < 13; j++) {
                double jdy = 0 + j * (4.0 / 13.0);
                result[i][j] = bi.getValue(source, idx, jdy);
            }
            System.out.printf("Result (%3.1f, %3.1f) : %3.1f%n", idx, idx, bi.getValue(source, idx, idx));
        }
        return result;
    }

    public static class linInterpolator {
        public static double getValue(double[] p, double x) {
            //set P0 and P1
            double p0;
            double p1;
            int xi = (int) x;
            x = x - xi;
            //if xi = 0
            if (x == 0) {
                //Log.e("xi becomes zero"," "+xi);
                p0 = p[xi];
                p1 = p0;
            } else {
                p0 = p[Math.max(0, xi)];
                p1 = p[Math.min(p.length - 1, xi + 1)];
            }
            //implement padding of variables
            if (p0 < 100 && p1 < 100) {
                //do nothing
            } else if (p0 < 100 && p1 > 100) {
                //make p1 equal to p0
                p1 = p0;
            } else if (p0 > 100 && p1 > 100) {
                //do nothing
            } else if (p0 > 100 && p1 < 100) {
                Log.e("Interpolation", "A case that is not expected is occuring");
            }
            return (p1 - p0) * x + p0;
        }
    }

    public static class BilinearInterpolator extends linInterpolator {
        private double[] arr = new double[2];

        public double getValue(double[][] p, double x, double y) {
            int xi = (int) x;
            x = x - xi;
            if (x == 0) {
                //Log.e("xi becomes zero"," "+xi);
                arr[0] = getValue(p[xi], y);
                arr[1] = arr[0];
            } else {
                arr[0] = getValue(p[Math.max(0, xi)], y);
                arr[1] = getValue(p[Math.min(p.length - 1, xi + 1)], y);
            }
            //change array values
            if (arr[0] < 100 && arr[1] < 100) {
                //do nothing
            } else if (arr[0] < 100 && arr[1] > 100) {
                //make p1 equal to p0
                arr[1] = arr[0];
            } else if (arr[0] > 100 && arr[1] > 100) {
                //do nothing
            } else if (arr[0] > 100 && arr[1] < 100) {
                Log.e("Interpolation", "A case that is not expected is occuring");
            }
            return getValue(arr, x);
        }
    }

    public double[][] postProcess(double[][] quad, String testPattern, int quadNo) {
        double[][] resultquad;
        switch (testPattern) {
            case "24-2":
                if (quadNo == 1 || quadNo == 4) {
                    int[] paddingVals = new int[]{11, 11, 11, 10, 10, 9, 8, 7, 6, 5, 4, 3, 2};
                    for (int i = 0; i < paddingVals.length; i++) {
                        quad[i] = postProcessColumn(quad[i], paddingVals[i]);
                    }
                } else {
                    int[] paddingVals = new int[]{11, 11, 11, 10, 10, 9, 8, 7, 6, 5, 3, 0, 0};
                    for (int i = 0; i < paddingVals.length; i++) {
                        quad[i] = postProcessColumn(quad[i], paddingVals[i]);
                    }
                }
                break;
            case "30-2":
                int[] paddingVals = new int[]{13, 13, 13, 13, 12, 12, 11, 11, 10, 9, 8, 6, 4};
                for (int i = 0; i < paddingVals.length; i++) {
                    quad[i] = postProcessColumn(quad[i], paddingVals[i]);
                }
                break;
            case "10-2":
                int[] paddingVals_1 = new int[]{13, 13, 13, 13, 12, 12, 11, 11, 10, 9, 8, 6, 4};
                for (int i = 0; i < paddingVals_1.length; i++) {
                    quad[i] = postProcessColumn(quad[i], paddingVals_1[i]);
                }
        }
        return quad;
    }

    public double[] postProcessColumn(double[] source, int paddingUpTo) {
        double[] resultRow;
        for (int i = 0; i < source.length; i++) {
            if (source[i] > 100 && i > 1 && i < paddingUpTo) {
                source[i] = source[i - 1];
            }
            if (i > paddingUpTo - 1) {
                source[i] = 1000;
            }
        }
        return source;
    }

    public double[][][] getGreyScaleVals(PerimetryObject_V2.FinalPerimetryResultObject finalPerimetryResultObject) {
        double[][][] quadrantVals = new double[][][]{{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}}, {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}}, {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}}, {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}}};
        for (int i = 0; i < 4; i++) {
            double[][] quad_source = getsourceQuad(finalPerimetryResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements, i + 1, finalPerimetryResultObject.Series.PatternSequence.CodeMeaning);
            quadrantVals[i] = getBilenar(quad_source);
            CommonUtils.writeToFile(Arrays.deepToString(quadrantVals[i]), "quad " + i);
            quadrantVals[i] = postProcess(quadrantVals[i], finalPerimetryResultObject.Series.PatternSequence.CodeMeaning, i + 1);
        }
        return quadrantVals;
    }
}
//todo : remove this functionality
                        /*ResultStore resultStore54 = new ResultStore(54);
                        TestMeasurementsInfo.VisualFieldTestPointSequence = new TestPointInformation[54];
                        TestPointInformation testPointInformation = new TestPointInformation();
                        for(int i=0;i<54;i++){
                            TestMeasurementsInfo.VisualFieldTestPointSequence[i] = new TestPointInformation();
                        }
                        for (int i = 0; i < 54; i++) {

                            TestMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate = resultStore54.list[i][0];
                            TestMeasurementsInfo.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate = resultStore54.list[i][1];
                            TestMeasurementsInfo.VisualFieldTestPointSequence[i].StimulusResults = "Seen";
                            TestMeasurementsInfo.VisualFieldTestPointSequence[i].SensitivityValue = resultStore54.resultVals[i];
                            TestMeasurementsInfo.VisualFieldTestPointSequence[i].RetestStimulusSeen = "NO";
                            TestMeasurementsInfo.VisualFieldTestPointSequence[i].RetestSensitivityValue = 0;
                            TestMeasurementsInfo.VisualFieldTestPointSequence[i].QuantifiedDefect = 0;

                        }
                        SeriesInfo.PatternSequence.CodeMeaning = "24-2";
                        //one function to do this all
                        double[][][] quadrants = interpolation.getGreyScaleVals(finalresultObj);
                        for(int i =0;i<13;i++){
                            for (int j=0;j<13;j++) {
                                Log.e("InterPolationq", i + " " +j+" "+ quadrants[0][i][j]);
                            }
                        }
                        //get quadrants
                        double [][] quad1_source = interpolation.getsourceQuad(finalresultObj.Measurements.VisualFieldStaticPerimetryTestMeasurements,1,"24-2");
                        double [][] quad2_source = interpolation.getsourceQuad(finalresultObj.Measurements.VisualFieldStaticPerimetryTestMeasurements,2,"24-2");
                        double [][] quad3_source = interpolation.getsourceQuad(finalresultObj.Measurements.VisualFieldStaticPerimetryTestMeasurements,3,"24-2");
                        double [][] quad4_source = interpolation.getsourceQuad(finalresultObj.Measurements.VisualFieldStaticPerimetryTestMeasurements,4,"24-2");
                        double[][] quad1_vals = interpolation.getBilenar(quad1_source);
                        double[][] quad2_vals = interpolation.getBilenar(quad2_source);
                        double[][] quad3_vals = interpolation.getBilenar(quad3_source);
                        double[][] quad4_vals = interpolation.getBilenar(quad4_source);
                        for(int i =0;i<13;i++){
                            for (int j=0;j<13;j++) {
                                Log.e("InterPolation", i + " " +j+" "+ quad1_vals[i][j]);
                            }
                        }
                        double[] column1 = quad1_vals[0];
                        for (int i=0;i<13;i++){
                            Log.e("Interpolation",i+" "+column1[i]);
                        }
                        //post process the quadrants
                        double[][] quad1_postProcess = interpolation.postProcess(quad1_vals,"24-2",1);
                        double[][] quad2_postProcess = interpolation.postProcess(quad2_vals,"24-2",2);
                        double[][] quad3_postProcess = interpolation.postProcess(quad3_vals,"24-2",3);
                        double[][] quad4_postProcess = interpolation.postProcess(quad3_vals,"24-2",4);
                        for(int i =0;i<13;i++){
                            for (int j=0;j<13;j++) {
                                Log.e("InterPolationf", i + " " +j+" "+ quad1_postProcess[i][j]);
                            }
                        }*/