package com.agyohora.mobileperitc.utils;

import android.util.Log;

import com.agyohora.mobileperitc.store.NormativeData;
import com.agyohora.mobileperitc.store.PerimetryObject;
import com.agyohora.mobileperitc.store.PerimetryObject_V2;

public final class VFI_Utils {

    public static int returnTestCode(String eye, String pattern, String strategy) {
        int pattern_stratNumber = 10000;
        if (eye.equals("Left Eye")) {
            pattern_stratNumber = pattern_stratNumber + 100;
        } else {
            pattern_stratNumber = pattern_stratNumber + 200;
        }
        //based on test pattern
        switch (pattern) {
            case "24-2":
                pattern_stratNumber = pattern_stratNumber + 10;
                break;
            case "30-2":
                pattern_stratNumber = pattern_stratNumber + 20;
                break;
            case "10-2":
                pattern_stratNumber = pattern_stratNumber + 30;
                break;
            case "Macula":
                pattern_stratNumber = pattern_stratNumber + 40;
                break;
            default:
                Log.d("SomeTag", "PatternSequence is not caught");
                break;

        }
        //based on test stretegy
        switch (strategy) {
            case "Screening":
                pattern_stratNumber = pattern_stratNumber + 1;
                break;
            case "Full Threshold":
                pattern_stratNumber = pattern_stratNumber + 2;
                break;
            case "Elisar Zest":
                pattern_stratNumber = pattern_stratNumber + 3;
                break;
            case "Elisar Fast":
                pattern_stratNumber = pattern_stratNumber + 4;
                break;
            case "Elisar Zest*":
                pattern_stratNumber = pattern_stratNumber + 3;
                break;
            case "Custom Zest":
                pattern_stratNumber = pattern_stratNumber + 3;
                break;
            case "Custom FT":
                pattern_stratNumber = pattern_stratNumber + 2;
                break;
            case "Custom Fast":
                pattern_stratNumber = pattern_stratNumber + 4;
                break;
            case "Custom Zest1":
                pattern_stratNumber = pattern_stratNumber + 3;
                break;

        }
        return pattern_stratNumber;
    }

    public static PerimetryObject_V2.FinalPerimetryResultObject upgradePerimetryObject(PerimetryObject.FinalPerimetryResultObject oldObject) {
        PerimetryObject_V2.FinalPerimetryResultObject newObject = new PerimetryObject_V2.FinalPerimetryResultObject();

        //Series Info
        newObject.Series.PerformedProcedureStepEndDate = oldObject.Series.PerformedProcedureStepEndDate;
        newObject.Series.PerformedProcedureStepEndTime = oldObject.Series.PerformedProcedureStepEndTime;

        //Equipment Info
        //todo : Change to capture from shared preferences
        newObject.Equipment.Manufacturer = "ELISAR";
        newObject.Equipment.InsititutionName = "DRAGARWAL";
        newObject.Equipment.InstitutionAddress = "No19";
        newObject.Equipment.StationName = "NoName";
        newObject.Equipment.InstitutionalDepartmentName = "Ophthal";
        newObject.Equipment.ManufacturerModelName = "V1";
        newObject.Equipment.DeviceSerialNumber = CommonUtils.getHotSpotId();
        newObject.Equipment.SoftwareVersion = CommonUtils.getVersionName();
        newObject.Equipment.DateOfLastCalibration = "20180215";
        newObject.Equipment.TimeOfLastCalibration = "000000";

        //Define Test parameters
        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.VisualFieldHorizontalExtent = 30;
        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.VisualFieldVerticalExtent = 30;
        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.VisualFieldShape = "Circle";
        //Screening test mode sequence is not filled.
        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.MaximumStimulusLuminance = 400;
        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.BackgroundLuminance = 10;

        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.StimulusColorCodeSequence.CodeValue = "G-A12B";
        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.StimulusColorCodeSequence.CodeSequenceDesignator = "SRT";
        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.StimulusColorCodeSequence.CodeVersion = "20100827";
        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.StimulusColorCodeSequence.CodeMeaning = "White";

        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.BackgroundIlluminationColorCodeSequence.CodeValue = "G-A12B";
        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.BackgroundIlluminationColorCodeSequence.CodeSequenceDesignator = "SRT";
        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.BackgroundIlluminationColorCodeSequence.CodeVersion = "20100827";
        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.BackgroundIlluminationColorCodeSequence.CodeMeaning = "White";

        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.StimulusArea = 0.23;
        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.StimulusPresentationTime = 150;


        newObject.Patient.PatientName = oldObject.Patient.PatientName;
        newObject.Patient.PatientUID = oldObject.Patient.PatientUID;
        newObject.Patient.PatientID = oldObject.Patient.PatientID;
        newObject.Patient.PatientBirthDate = oldObject.Patient.PatientBirthDate;
        newObject.Patient.PatientSex = oldObject.Patient.PatientSex;
        newObject.Series.Laterality = oldObject.Series.Laterality;
        newObject.Series.StrategySequence.CodeMeaning = oldObject.Series.StrategySequence.CodeMeaning;
        newObject.Series.PatternSequence.CodeMeaning = oldObject.Series.PatternSequence.CodeMeaning;

        newObject.Series.duration = oldObject.Series.duration;

        newObject.Series.PatternSequence.CodeMeaning = oldObject.Series.PatternSequence.CodeMeaning;

        newObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationRightEyeSequence.PupilSize = oldObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationRightEyeSequence.PupilSize;
        newObject.Measurements.VisualFieldStaticPerimeteryTestParameters.BackgroundIlluminationColorCodeSequence.CodeMeaning = oldObject.Measurements.VisualFieldStaticPerimeteryTestParameters.BackgroundIlluminationColorCodeSequence.CodeMeaning;
        newObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylinderAxis = oldObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylinderAxis;
        newObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylindricalLensPower = oldObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylindricalLensPower;
        newObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.SphericalLensPower = oldObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.SphericalLensPower;

        newObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.FovealSensitivityMeasured = oldObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.FovealSensitivityMeasured;
        newObject.Measurements.VisualFieldStaticPerimetryTestResults.ResultNormalSequence.GlobalDeviationFromNormal = oldObject.Measurements.VisualFieldStaticPerimetryTestResults.ResultNormalSequence.GlobalDeviationFromNormal;
        //String prob = " P < 0.5";
        newObject.Measurements.VisualFieldStaticPerimetryTestResults.ResultNormalSequence.LocalizedDeviationFromNormal = oldObject.Measurements.VisualFieldStaticPerimetryTestResults.ResultNormalSequence.LocalizedDeviationFromNormal;

        newObject.Measurements.VisualFieldStaticPerimetryTestReliability.FixationSequence.FixationCheckedQuantity = oldObject.Measurements.VisualFieldStaticPerimetryTestReliability.FixationSequence.FixationCheckedQuantity;
        newObject.Measurements.VisualFieldStaticPerimetryTestReliability.FixationSequence.PatientNotProperlyFixatedQuantity = oldObject.Measurements.VisualFieldStaticPerimetryTestReliability.FixationSequence.PatientNotProperlyFixatedQuantity;

        newObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.PositiveCatchTrialsQuantity = oldObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.PositiveCatchTrialsQuantity;
        newObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.FalsePositivesQuantity = oldObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.FalsePositivesQuantity;

        newObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.NegativeCatchTrialsQuantity = oldObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.NegativeCatchTrialsQuantity;
        newObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.FalseNegativesQuantity = oldObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.FalseNegativesQuantity;

        newObject.TestResultsInfo.ResultNormalSequence.GlobalDeviationProbabilityNormalsFlag = oldObject.TestResultsInfo.ResultNormalSequence.GlobalDeviationProbabilityNormalsFlag;


        newObject.TestResultsInfo.ResultNormalSequence.GlobalDeviationProbabilitySequence.GlobalDeviationProbability = oldObject.TestResultsInfo.ResultNormalSequence.GlobalDeviationProbabilitySequence.GlobalDeviationProbability;
        newObject.TestResultsInfo.ResultNormalSequence.LocalDeviationProbabilitySequence.LocalDeviationProbability = oldObject.TestResultsInfo.ResultNormalSequence.LocalDeviationProbabilitySequence.LocalDeviationProbability;

        newObject.Series.PerformedProcedureStepStartTime = oldObject.Series.PerformedProcedureStepStartTime;
        newObject.Series.PerformedProcedureStepEndTime = oldObject.Series.PerformedProcedureStepEndTime;

        int length = oldObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence.length;
        String eye = newObject.Series.Laterality.equals("L") ? "Left Eye" : "Right Eye";
        String dob = newObject.Patient.PatientBirthDate;
        String age = CommonUtils.getAge(dob);
        String strategy = newObject.Series.StrategySequence.CodeMeaning;
        String pattern = newObject.Series.PatternSequence.CodeMeaning;
        int pattern_stratNumber = VFI_Utils.returnTestCode(eye, pattern, strategy);

        switch (pattern) {
            case "24-2":
                PerimetryObject_V2.TestMeasurementsInfo.VisualFieldTestPointSequence = new PerimetryObject_V2.TestPointInformation[54];
                for (int i = 0; i < 54; i++) {
                    PerimetryObject_V2.TestMeasurementsInfo.VisualFieldTestPointSequence[i] = new PerimetryObject_V2.TestPointInformation();
                }
                break;
            case "30-2":
                PerimetryObject_V2.TestMeasurementsInfo.VisualFieldTestPointSequence = new PerimetryObject_V2.TestPointInformation[76];
                for (int i = 0; i < 76; i++) {
                    PerimetryObject_V2.TestMeasurementsInfo.VisualFieldTestPointSequence[i] = new PerimetryObject_V2.TestPointInformation();
                }
                break;
            case "10-2":
                PerimetryObject_V2.TestMeasurementsInfo.VisualFieldTestPointSequence = new PerimetryObject_V2.TestPointInformation[68];
                for (int i = 0; i < 68; i++) {
                    PerimetryObject_V2.TestMeasurementsInfo.VisualFieldTestPointSequence[i] = new PerimetryObject_V2.TestPointInformation();
                }
                break;
            case "Macula":
                PerimetryObject_V2.TestMeasurementsInfo.VisualFieldTestPointSequence = new PerimetryObject_V2.TestPointInformation[16];
                for (int i = 0; i < 16; i++) {
                    PerimetryObject_V2.TestMeasurementsInfo.VisualFieldTestPointSequence[i] = new PerimetryObject_V2.TestPointInformation();
                }
                break;
            default:
                Log.d("SomeTag", "PatternSequence is not caught");
                break;

        }
        PerimetryObject_V2.TestResultsInfo.VisualFieldGlobalResultsIndexSequence = new PerimetryObject_V2.VisualFieldGlobalResultsIndexSequence[]{new PerimetryObject_V2.VisualFieldGlobalResultsIndexSequence(), new PerimetryObject_V2.VisualFieldGlobalResultsIndexSequence()};
        //for (int i = 0; i < 2; i++) {
        PerimetryObject_V2.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[0] = new PerimetryObject_V2.VisualFieldGlobalResultsIndexSequence();
        PerimetryObject_V2.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1] = new PerimetryObject_V2.VisualFieldGlobalResultsIndexSequence();

        PerimetryObject_V2.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[0].DataObservationSequence = new PerimetryObject_V2.DataObservationSequence();
        PerimetryObject_V2.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence = new PerimetryObject_V2.DataObservationSequence();

        double[] resList = new double[length];

        for (int i = 0; i < length; i++) {

            newObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate = oldObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate;
            newObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate = oldObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate;
            newObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].SensitivityValue = oldObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].SensitivityValue;
            newObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.AgeCorrectedSensitivityDeviationValue = oldObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.AgeCorrectedSensitivityDeviationValue;
            newObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.AgeCorrectedSensitivityProbabilityDeviationValue = oldObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.AgeCorrectedSensitivityProbabilityDeviationValue;
            newObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.GeneralizedDefectCorrectedSensitivityDeviationValue = oldObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.GeneralizedDefectCorrectedSensitivityDeviationValue;
            newObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.GeneralizedDefectCorrectedSensitivityDeviationProbabilityValue = oldObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.GeneralizedDefectCorrectedSensitivityDeviationProbabilityValue;
            newObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].StimulusResults = oldObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].StimulusResults;

            int x = (int) newObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate;
            int y = (int) newObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate;
            int s = (int) newObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].SensitivityValue;

            resList[i] = s;
        }


        NormativeData normativeData = new NormativeData();
        double[][] normDevVals = normativeData.getNormDeviationVector(Integer.parseInt(age), pattern_stratNumber, resList);

        newObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[0].DataObservationSequence.TextValue = oldObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence.DataObservationSequence.TextValue;
       // CommonUtils.writeToBugFixLogFile(" Before " + newObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[0].DataObservationSequence.TextValue + " VFI " + normDevVals[6][1]);
        newObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence.ValueType = "DECIMAL STRING";
        newObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence.TextValue = "" + (int) normDevVals[6][1];
        newObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence.numericValue = (int) normDevVals[6][1];
        newObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence.floatingPointValue = normDevVals[6][1];

        // newObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[0].DataObservationSequence.TextValue = "Checkmeout";
        CommonUtils.writeToBugFixLogFile(" From upgrade normal devs old GHT " + oldObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence.DataObservationSequence.TextValue + " VFI " + normDevVals[6][1]);
        CommonUtils.writeToBugFixLogFile(" From upgrade new object GHT " + newObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[0].DataObservationSequence.TextValue);
        CommonUtils.writeToBugFixLogFile(" VFI ValueType " + newObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence.ValueType);
        CommonUtils.writeToBugFixLogFile(" VFI TextValue " + newObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence.TextValue);
        CommonUtils.writeToBugFixLogFile(" VFI numericValue " + newObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence.numericValue);
        CommonUtils.writeToBugFixLogFile(" VFI floatingPointValue " + newObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence.floatingPointValue);
        CommonUtils.writeToBugFixLogFile("\n");
        return newObject;

    }

}
