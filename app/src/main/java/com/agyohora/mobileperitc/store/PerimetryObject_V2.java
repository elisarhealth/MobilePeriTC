package com.agyohora.mobileperitc.store;

import java.util.ArrayList;

public class PerimetryObject_V2 {

    public static class FinalPerimetryResultObject {
        public static PatientInfo Patient;
        public static StudyInfo Study;
        public static SeriesInfo Series;
        public static EquipmentInfo Equipment;
        public static MeasurementInfo Measurements;
        public static TestResultsInfo TestResultsInfo;

        public FinalPerimetryResultObject() {
            Patient = new PatientInfo();
            Study = new StudyInfo();
            Series = new SeriesInfo();
            Equipment = new EquipmentInfo();
            Measurements = new MeasurementInfo();
            TestResultsInfo = new TestResultsInfo();
        }
    }

    public static class PatientInfo {
        public static String PatientName;
        public static String PatientID;
        public static String PatientUID;
        public static String PatientBirthDate;
        public static String PatientSex;
        static String PatientIdentityRemoved;
        static String DeIdentificationMethod;
        static CodeSequence DeIdentificationMethodCodeSequence;

        PatientInfo() {
            //PatientName = "A";
            //PatientID = "AB";
            //PatientUID = "awdas";
            //PatientBirthDate = "03101989";
            //PatientSex = "M";
            //PatientIdentityRemoved = "NO";
            //DeIdentificationMethod = "Null";
            DeIdentificationMethodCodeSequence = new CodeSequence();
            //DeIdentificationMethodCodeSequence.CodeSequenceDesignator="NA";
            //DeIdentificationMethodCodeSequence.CodeMeaning = "NA";
            //DeIdentificationMethodCodeSequence.CodeValue = "NA";
            //DeIdentificationMethodCodeSequence.CodeVersion = "NA";
        }

    }

    public static class StudyInfo {
        static String StudyInstanceUID;
        static String StudyDate;
        static String StudyTime;
        static String ReferringPhysiciansName;
        static String StudyID;
        static String AccessionNumber;
        static String StudyDescription;
        static CodeSequence RequestingServiceCodeSequence;


        StudyInfo() {
            /*StudyInstanceUID = "NA";
            StudyDate = "20180215";
            StudyDate = "111111";
            ReferringPhysiciansName = "Dr Zhivago";
            StudyID = "StudyVal";
            AccessionNumber = null;
            StudyDescription = "VisualFieldTest";*/
            RequestingServiceCodeSequence = new CodeSequence();
            /*RequestingServiceCodeSequence.CodeValue = null;
            RequestingServiceCodeSequence.CodeSequenceDesignator = null;
            RequestingServiceCodeSequence.CodeMeaning = null;
            RequestingServiceCodeSequence.CodeVersion = null;*/
        }
    }

    //class for series infomration
    public static class SeriesInfo {
        static String Modality;
        static String SeriesInstanceUID;
        static String SeriesNumber;
        public static String Laterality;
        public static boolean isImported;
        public static String importedDuration;
        public static String duration;
        static String SeriesDate;
        static String SeriesTime;
        static String PerformingPhysiciansName;
        static String OperatorName;
        static String PerformedProcedureStepID; //only a single step is performed per procedure
        public static String PerformedProcedureStepStartDate;
        public static String PerformedProcedureStepStartTime;//subtract this and End time
        public static String PerformedProcedureStepEndDate;
        public static String PerformedProcedureStepEndTime;
        static String PerformedProcedureStepDescription;
        static ArrayList<CodeSequence> PerformedProtocolCodeSequence;//will be built at the time of result push
        public static CodeSequence PatternSequence;//dynamic in relavant field
        public static CodeSequence StrategySequence;//dynamic in relaveat field


        SeriesInfo() {
            /*Modality = "OPV";
            SeriesInstanceUID = "UI";
            SeriesNumber = "00001";
            Laterality = "L";
            SeriesDate = "20180223";
            SeriesTime = "220303.0000";
            PerformingPhysiciansName = "Trafalgar D. Law";
            OperatorName = "Chopper";//Yay one piece
            PerformedProcedureStepID = "0001";
            PerformedProcedureStepStartDate = null;
            PerformedProcedureStepEndTime = null;
            PerformedProcedureStepStartTime = null;
            PerformedProcedureStepEndDate = null;
            PerformedProcedureStepDescription = null;*/
            PatternSequence = new CodeSequence();
            /*PatternSequence.CodeValue = "24-2";
            PatternSequence.CodeMeaning = "24 dash two test";
            PatternSequence.CodeVersion = "0001";
            PatternSequence.CodeSequenceDesignator = "ELISAR";*/
            StrategySequence = new CodeSequence();
            /*StrategySequence.CodeValue = "24-2";
            StrategySequence.CodeMeaning = "24 dash two test";
            StrategySequence.CodeVersion = "0001";
            StrategySequence.CodeSequenceDesignator = "ELISAR";*/
            PerformedProtocolCodeSequence = new ArrayList<>();
            //PerformedProtocolCodeSequence.add(StrategySequence);
        }
    }

    //class for EquipmentInformation
    public static class EquipmentInfo {
        public static String Manufacturer;
        public static String InsititutionName;
        public static String InstitutionAddress;
        public static String StationName;
        public static String InstitutionalDepartmentName;
        public static String ManufacturerModelName;
        public static String DeviceSerialNumber;
        public static String SoftwareVersion;
        public static String DateOfLastCalibration;
        public static String TimeOfLastCalibration;

        /*EquipmentInfo(){
            Manufacturer = "Elisar";
            InsititutionName = "NotRegistered";
            InstitutionAddress="NotAvailable";
            StationName="FieldNotUsed";
            InstitutionalDepartmentName="Ophthalmology";
            ManufacturerModelName="ElisarAVA001";
            DeviceSerialNumber="HMD001_TC001";
            SoftwareVersion="SWV001";
            DateOfLastCalibration="20180215";
            TimeOfLastCalibration="091025";
        }*/
    }

    //class for Measurements
    public static class MeasurementInfo {
        static SopCommon SOPCommon;
        public static TestParameterInfo VisualFieldStaticPerimeteryTestParameters;
        public static TestReliabilityInfo VisualFieldStaticPerimetryTestReliability;
        public static TestMeasurementsInfo VisualFieldStaticPerimetryTestMeasurements;
        public static TestResultsInfo VisualFieldStaticPerimetryTestResults;
        public static PatientClinicalInfo OphthalmicPatientClinicalInformationAndTestLensParameters;

        MeasurementInfo() {
            SOPCommon = new SopCommon();
            VisualFieldStaticPerimeteryTestParameters = new TestParameterInfo();
            VisualFieldStaticPerimetryTestReliability = new TestReliabilityInfo();
            VisualFieldStaticPerimetryTestMeasurements = new TestMeasurementsInfo();
            VisualFieldStaticPerimetryTestResults = new TestResultsInfo();
            OphthalmicPatientClinicalInformationAndTestLensParameters = new PatientClinicalInfo();
        }
    }

    //class for SOPCommon
    static class SopCommon {
        private static String Placeholder;

        /*SopCommon(){
            Placeholder = "placeholder";
        }*/
    }

    //class for test parameters
    public static class TestParameterInfo {
        public static double VisualFieldHorizontalExtent;
        public static double VisualFieldVerticalExtent;
        public static String VisualFieldShape;
        static CodeSequence ScreeningTestModeCodeSequence;
        public static double MaximumStimulusLuminance;
        public static double BackgroundLuminance;
        public static CodeSequence StimulusColorCodeSequence;
        public static CodeSequence BackgroundIlluminationColorCodeSequence;//make dynamic
        public static double StimulusArea;
        public static double StimulusPresentationTime;

        TestParameterInfo() {
            /*VisualFieldHorizontalExtent = 56;
            VisualFieldVerticalExtent = 60;
            VisualFieldShape = "SQUARE";*/
            ScreeningTestModeCodeSequence = new CodeSequence();
            /*ScreeningTestModeCodeSequence.CodeValue = "NA";
            ScreeningTestModeCodeSequence.CodeMeaning = "Nothing";
            ScreeningTestModeCodeSequence.CodeVersion = "NA";
            ScreeningTestModeCodeSequence.CodeSequenceDesignator = "NA";
            MaximumStimulusLuminance = 400;
            BackgroundLuminance = 10;*/
            StimulusColorCodeSequence = new CodeSequence();
            /*StimulusColorCodeSequence.CodeValue = "G-A12B";
            StimulusColorCodeSequence.CodeSequenceDesignator = "SRT";
            StimulusColorCodeSequence.CodeVersion = "20100827";
            StimulusColorCodeSequence.CodeMeaning = "White"; //Todo : Change codesequencedesignator to CodingSchemeDesignator
            //Todo : CodingSchemeVersion */
            BackgroundIlluminationColorCodeSequence = new CodeSequence();
            /*BackgroundIlluminationColorCodeSequence.CodeValue = "G-A12B";
            BackgroundIlluminationColorCodeSequence.CodeSequenceDesignator = "SRT";
            BackgroundIlluminationColorCodeSequence.CodeVersion = "20100827";
            BackgroundIlluminationColorCodeSequence.CodeMeaning = "White";*/
            //StimulusArea = 0; //TOdo : Define how to say 0.43 degrees;
            //StimulusPresentationTime = 150;


        }
    }

    //class for test reliability
    public static class TestReliabilityInfo {
        public static fixationSequence FixationSequence;
        public static catchTrialSequence VisualFieldCatchTrialSequence;
        static int StimuliRetestingQuantity;
        static String PatientReliabilityIndicator;
        static String CommentOnPatientPerformance;
        static testReliabilityGlobIndices VisualFieldTestReliabilityGlobalIndexSequence;

        TestReliabilityInfo() {
            FixationSequence = new fixationSequence();
            VisualFieldCatchTrialSequence = new catchTrialSequence();
            /*StimuliRetestingQuantity = 0;
            PatientReliabilityIndicator = "GOOD";
            CommentOnPatientPerformance = null;*/
            VisualFieldTestReliabilityGlobalIndexSequence = new testReliabilityGlobIndices();

        }
    }

    //fixation
    public static class fixationSequence {
        static CodeSequence FixationMonitoringCodeSequence;
        public static int FixationCheckedQuantity; //fixation denominater
        public static int PatientNotProperlyFixatedQuantity;//numerator
        static String ExcessiveFixationLossesDataFlag;
        static String ExcessiveFixationLosses;

        fixationSequence() {
            FixationMonitoringCodeSequence = new CodeSequence();
            /*FixationMonitoringCodeSequence.CodeValue = "111844";
            FixationMonitoringCodeSequence.CodeSequenceDesignator = "DCM";
            FixationMonitoringCodeSequence.CodeVersion = "20100827";
            FixationMonitoringCodeSequence.CodeMeaning = "Blind Spot Monitoring";
            FixationCheckedQuantity = 0;
            PatientNotProperlyFixatedQuantity = 0;
            ExcessiveFixationLossesDataFlag = "NO";
            ExcessiveFixationLosses = "NO";*/
        }
    }

    public static class catchTrialSequence {
        static String CatchTrialsDataFlag;
        public static int NegativeCatchTrialsQuantity;// denominator
        public static int FalseNegativesQuantity; // numerator
        static String FalseNegativesEstimateFlag;//fN
        static double FalseNegativesEstimate;
        static String ExcessiveFalseNegativesDataFlag;
        static String ExcessiveFalseNegatves;
        public static int PositiveCatchTrialsQuantity;//denominator
        public static int FalsePositivesQuantity;//numerator
        static String FalsePositivesEstimateFlag;//fp
        static double FalsePositivesEstimate;
        static String ExcessiveFalsePositivessDataFlag;
        static String ExcessiveFalsePositives;

        /*catchTrialSequence(){
            CatchTrialsDataFlag = "YES";
            NegativeCatchTrialsQuantity = 1;
            FalseNegativesQuantity = 0;
            FalseNegativesEstimateFlag = "YES";
            FalseNegativesEstimate = ((double)FalseNegativesQuantity/(double)NegativeCatchTrialsQuantity)*100;
            ExcessiveFalseNegativesDataFlag = "NO";
            ExcessiveFalseNegatives = "NO";
            PositiveCatchTrialsQuantity = 1;
            FalsePositivesQuantity = 0;
            FalsePositivesEstimateFlag = "YES";
            FalsePositivesEstimate = ((double)FalsePositivesQuantity/(double)PositiveCatchTrialsQuantity)*100;
            ExcessiveFalsePositivessDataFlag = "YES";
            ExcessiveFalsePositives = "NO";
        }*/
    }

    public static class testReliabilityGlobIndices {
        static String value;
        /*testReliabilityGlobIndices(){
            value = null;
        }*/
    }

    // class for test measurements
    public static class TestMeasurementsInfo {
        static String MeasurementLaterality;
        static String PresentedVisualStimuliDataFlag;
        static int NumberOfVisualStimuli;
        static double VisualFieldTestDuration;
        public static String FovealSensitivityMeasured;
        public static double FovealSensitivity;
        static String FovealPointNormativeDataFlag;
        static double FovealPointProbabilityValue;
        static String ScreeningBaselineMeasured;
        static screeningBaseLineMeasuredSequence ScreeningBaselineMeasuredSequence;
        static String BlindSpotLocalized;
        static double BlindSpotXCoordinate;
        static double BlindSpotYCoordinate;
        static double MinimumSensitivityValue;
        static String TestPointNormalsDataFlag;
        static double E_IPDSetting;
        static DatasetSequenceMacro TestPointNormalsDataSequence;
        static AlgorithmIdentification AgeCorrectedSensitivityDeviationAlgorithmSequence;
        static AlgorithmIdentification GeneralizedDefectSensitivityDeviationAlgorithmSequence;
        public static TestPointInformation[] VisualFieldTestPointSequence;

        TestMeasurementsInfo() {
            /*MeasurementLaterality = "L" ;
            PresentedVisualStimuliDataFlag = "YES";
            NumberOfVisualStimuli = 0;
            VisualFieldTestDuration = 0;
            FovealSensitivityMeasured = "NO";
            FovealSensitivity = 0;
            FovealPointNormativeDataFlag = "NO";
            ScreeningBaselineMeasured = "NO";
            BlindSpotLocalized = "YES";
            BlindSpotXCoordinate = 3;
            BlindSpotYCoordinate = 15;
            MinimumSensitivityValue = 0;
            TestPointNormalsDataFlag = "YES";
            E_IPDSetting = -10;*/
            ScreeningBaselineMeasuredSequence = new screeningBaseLineMeasuredSequence();
            TestPointNormalsDataSequence = new DatasetSequenceMacro();
            AgeCorrectedSensitivityDeviationAlgorithmSequence = new AlgorithmIdentification();
            GeneralizedDefectSensitivityDeviationAlgorithmSequence = new AlgorithmIdentification();
            //VisualFieldTestPointSequence = new TestPointInformation[80];

        }
    }

    static class screeningBaseLineMeasuredSequence {
        static String ScreeningBaselineType;
        static double ScreeningBaselineValue;
    }

    //class for test point information
    public static class TestPointInformation {
        public double VisualFieldTestPointXCoordinate;
        public double VisualFieldTestPointYCoordinate;
        public String StimulusResults;
        public double SensitivityValue;
        public String RetestStimulusSeen;
        double RetestSensitivityValue;
        double QuantifiedDefect;
        public testPointNormalSequence VisualFieldTestPointNormalSequence;

        public TestPointInformation() {
            VisualFieldTestPointXCoordinate = 0;
            VisualFieldTestPointYCoordinate = 0;
            StimulusResults = "No";
            SensitivityValue = 0.0;
            RetestStimulusSeen = "No";
            RetestSensitivityValue = 0.0;
            QuantifiedDefect = 0;
            VisualFieldTestPointNormalSequence = new testPointNormalSequence();
        }

        public class testPointNormalSequence {
            public double AgeCorrectedSensitivityDeviationValue;
            public double AgeCorrectedSensitivityProbabilityDeviationValue;
            public String GeneralizedDefectCorrectedSensitivityFlag;
            public double GeneralizedDefectCorrectedSensitivityDeviationValue;
            public double GeneralizedDefectCorrectedSensitivityDeviationProbabilityValue;
        }
    }

    //class for test results
    public static class TestResultsInfo {
        static double VisualFieldMeanSensitivity;
        static String VisualFieldTestNormalsFlag;
        public static ResultNormalSequence ResultNormalSequence;
        static String ShortTermFluctuationCalculated;
        static double ShortTermFluctuation;
        static String ShortTermFluctuationProbabilityCalculated;
        static double ShortTermFluctuationProbability;
        static String CorrectedLocalizedDeviationFromNormalProbabilityCalculated;
        static double CorrectedLocalizedDeviationFromNormalProbability;
        public static VisualFieldGlobalResultsIndexSequence[] VisualFieldGlobalResultsIndexSequence;

        TestResultsInfo() {
            ResultNormalSequence = new ResultNormalSequence();
            //VisualFieldGlobalResultsIndexSequence = new VisualFieldGlobalResultsIndexSequence[2];
        }

    }//class for test results

    public static class VisualFieldGlobalResultsIndexSequence {
        public DataObservationSequence DataObservationSequence;
        static String IndexNormalsFlag;

        public VisualFieldGlobalResultsIndexSequence() {
            DataObservationSequence = new DataObservationSequence();
        }
    }

    public static class DataObservationSequence {
        public String ValueType;
        public String TextValue;
        static CodeSequence ConceptName;
        public int numericValue;
        public double floatingPointValue;


        public DataObservationSequence() {
            ConceptName = new CodeSequence();
        }
    }

    public static class ResultNormalSequence {
        static String DataSetName;
        static String DataSetVersion;
        static String DatasetSource;
        static String DatasetDescription;
        public static double GlobalDeviationFromNormal;
        public static String GlobalDeviationProbabilityNormalsFlag;
        public static GlobalDeviationProbabilitySequence GlobalDeviationProbabilitySequence;
        public static double LocalizedDeviationFromNormal;
        public static LocalDeviationProbabilitySequence LocalDeviationProbabilitySequence;
        static String LocalizedDeviationProbabilityNormalsFlag;

        //TODO : Add Global Deviation Probability Sequence  and Localized Deviation Probability Sequence when data is available
        ResultNormalSequence() {
            GlobalDeviationProbabilitySequence = new GlobalDeviationProbabilitySequence();
            LocalDeviationProbabilitySequence = new LocalDeviationProbabilitySequence();
        }
    }//Normal data base information

    public static class GlobalDeviationProbabilitySequence {
        public static double GlobalDeviationProbability;
        static CodeSequence AlgorithmFamilyCodeSequence;
        static CodeSequence AlgorithmNameCodeSequence;
        static String AlgorithmName;
        static String AlgorithmVersion;

        GlobalDeviationProbabilitySequence() {
            AlgorithmFamilyCodeSequence = new CodeSequence();
            AlgorithmNameCodeSequence = new CodeSequence();
        }
    }

    public static class LocalDeviationProbabilitySequence {
        public static double LocalDeviationProbability;
        static CodeSequence AlgorithmFamilyCodeSequence;
        static CodeSequence AlgorithmNameCodeSequence;
        static String AlgorithmName;
        static String AlgorithmVersion;

        LocalDeviationProbabilitySequence() {
            AlgorithmFamilyCodeSequence = new CodeSequence();
            AlgorithmNameCodeSequence = new CodeSequence();
        }
    }

    //class for clinical Information
    public static class PatientClinicalInfo {
        public static EyeSequence OphthalmicPatientClinicalInformationRightEyeSequence;
        public static EyeSequence OphthalmicPatientClinicalInformationLeftEyeSequence;

        PatientClinicalInfo() {
            OphthalmicPatientClinicalInformationRightEyeSequence = new EyeSequence();
            OphthalmicPatientClinicalInformationLeftEyeSequence = new EyeSequence();
        }
    }

    public static class EyeSequence {
        public refractiveParametersUsed RefractiveParametersUsedOnPatientSequence;
        public double PupilSize;//pupil diameter
        String PupilDilated;
        double IntraOcularPressure;

        EyeSequence() {
            RefractiveParametersUsedOnPatientSequence = new refractiveParametersUsed();
        }

    }

    public static class refractiveParametersUsed {
        public double SphericalLensPower;
        public double CylindricalLensPower;
        public double CylinderAxis;
    }

    //class for CodeSequence. Being made because it is used multiple times
    public static class CodeSequence {
        public String CodeValue;
        public String CodeMeaning;
        public String CodeSequenceDesignator;
        public String CodeVersion;
    }

    //class for data set description
    static class DatasetSequenceMacro {
        private String DatasetName;
        private String DatasetVersion;
        private String DatasetSource;
        private String DatasetDescription;
    }

    //class for Algorithm description
    static class AlgorithmIdentification {
        private CodeSequence AlgorithmFamilyCodeSequence;
        private String AlgorithmName;
        private String AlgorithmVersion;
        private String AlgorithmParameters;
        private String AlgorithmSource;
    }
}
