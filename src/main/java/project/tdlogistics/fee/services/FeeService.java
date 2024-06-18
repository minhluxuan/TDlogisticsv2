package project.tdlogistics.fee.services;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.fee.entities.ServiceFee;
import project.tdlogistics.fee.entities.SpecialCase;
import project.tdlogistics.fee.entities.RangeMass;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class FeeService {

    private static final Map<String, List<String>> area;
    private static final Map<String, ServiceFee> data;

    static {
        ObjectMapper mapper = new ObjectMapper();
        try {
            area = mapper.readValue(new File("../repositories/newarea.json"), Map.class);
            data = mapper.readValue(new File("../repositories/newfee.json"), Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JSON files", e);
        }
    }

    public static double calculateFee(String serviceCode, String source, String destination, double mass,
                                      double increasingRateWhenBelongToRemoteArea, boolean isBelongToRemoteArea) {
        double resultFee = 0;
        String sourceArea = null;
        String desArea = null;
        ServiceFee transferService = data.get(serviceCode);

        for (String range : area.keySet()) {
            if (area.get(range).contains(source)) {
                sourceArea = range;
            }
            if (area.get(range).contains(destination)) {
                desArea = range;
            }
        }

        if (transferService != null) {
            if ("TTK".equals(serviceCode)) {
                if (sourceArea != null && sourceArea.equals(desArea)) {
                    resultFee = calculateFeeForRange(transferService.getInnerArea(), mass, transferService.getBlockStepIncre());
                } else if (isMiddleAreaCase(sourceArea, desArea)) {
                    resultFee = calculateFeeForRange(transferService.getOuterArea(), mass, transferService.getBlockStepIncre());
                } else {
                    resultFee = calculateFeeForRange(transferService.getSeperateArea(), mass, transferService.getBlockStepIncre());
                }
            } else {
                if (source.equals(destination)) {
                    resultFee = calculateFeeForRange(transferService.getInnerProvince(), mass, transferService.getBlockStepIncre());
                } else if (sourceArea != null && sourceArea.equals(desArea)) {
                    resultFee = calculateFeeForRange(transferService.getInnerArea(), mass, transferService.getBlockStepIncre());
                } else {
                    boolean isSpecialCase = false;
                    for (SpecialCase location : transferService.getSpecialCase()) {
                        if (source.equals(location.getFromProvince()) && destination.equals(location.getToProvince())) {
                            isSpecialCase = true;
                            resultFee = calculateFeeForRange(location.getDetailMass(), mass, transferService.getBlockStepIncre());
                            break;
                        }
                    }

                    if (!isSpecialCase) {
                        resultFee = calculateFeeForRange(transferService.getOrdinaryCase(), mass, transferService.getBlockStepIncre());
                    }
                }
            }
        }

        return isBelongToRemoteArea ? resultFee * (1 + increasingRateWhenBelongToRemoteArea) : resultFee;
    }

    private static boolean isMiddleAreaCase(String sourceArea, String desArea) {
        return ("NORTH_AREA".equals(sourceArea) && "MIDDLE_AREA".equals(desArea)) ||
               ("MIDDLE_AREA".equals(sourceArea) && "NORTH_AREA".equals(desArea)) ||
               ("SOUTH_AREA".equals(sourceArea) && "MIDDLE_AREA".equals(desArea)) ||
               ("MIDDLE_AREA".equals(sourceArea) && "SOUTH_AREA".equals(desArea));
    }

    private static double calculateFeeForRange(List<RangeMass> rangeMassList, double mass, double blockStepIncre) {
        for (RangeMass rangeMass : rangeMassList) {
            if ("INFINITY".equals(rangeMass.getToMass()) || (mass > rangeMass.getFromMass() && mass <= Double.parseDouble(rangeMass.getToMass()))) {
                if (rangeMass.getIncrementPerKilogram() != null) {
                    return rangeMass.getBaseFee() + Math.ceil((mass - rangeMass.getFromMass()) / blockStepIncre) * rangeMass.getIncrementPerKilogram();
                } else {
                    return rangeMass.getFee();
                }
            }
        }
        return 0;
    }
}

