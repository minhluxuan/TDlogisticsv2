package project.tdlogistics.agency_company.services;

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import project.tdlogistics.agency_company.entities.Agency;
import project.tdlogistics.agency_company.entities.District;
import project.tdlogistics.agency_company.entities.Ward;
import project.tdlogistics.agency_company.entities.placeholder.Response;
import project.tdlogistics.agency_company.repositories.*;

import java.util.regex.Pattern;
import java.util.List;
import java.util.Optional;

@Service
public class AgencyService {
    final String regexPersonnel = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$";
    private final AgencyRepository agencyRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final ProvinceRepository provinceRepository;

    @Autowired
    public AgencyService(AgencyRepository agencyRepository, ProvinceRepository provinceRepository,
            DistrictRepository districtRepository,
            WardRepository wardRepository) {
        this.agencyRepository = agencyRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
        this.provinceRepository = provinceRepository;
    }

    public Optional<Agency> checkExistAgency(String agency_id) {
        return agencyRepository.findById(agency_id);
    }

    public Optional<Agency> checkExistAgency(Agency criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Agency> example = Example.of(criteria, matcher);
        Optional<Agency> result = agencyRepository.findOne(example);
        if (result.isPresent()) {
            return Optional.of(result.get());
        } else {
            return Optional.empty();
        }
    }

    // public Optional<Agency> checkExistAgency() {
    // Agency agency = new Agency();
    // agency.setEmail("buucucquan1@gmail.com");
    // System.out.println("here3");
    // ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues()
    // .withMatcher("email", ExampleMatcher.GenericPropertyMatcher::exact);
    // Example<Agency> example = Example.of(agency, matcher);
    // // return agencyRepository.findOne(example);
    // return null;
    // }

    public boolean checkPostalCode(String province, String district, String postal_code) {

        Agency agencyTemp = new Agency();
        agencyTemp.setPostal_code(postal_code);

        if (checkExistAgency(agencyTemp).isPresent()) {
            throw new IllegalArgumentException("Mã bưu chính đã tồn tại");
        }

        if (agencyRepository.checkExistTableWithPostalCode(postal_code)) {
            throw new IllegalStateException("Mã bưu chính đã được khởi tạo trong database");
        }
        District resultFindingDistrict = districtRepository.findOneDistricsByProvinceAndDistrict(province, district);
        if (resultFindingDistrict == null) {
            throw new IllegalStateException("Mã bưu chính đã được khởi tạo trong database");
        }

        String databasePostalCode = resultFindingDistrict.getPostalCode();
        if (databasePostalCode == null || !databasePostalCode.substring(0, 4).equals(postal_code.substring(0, 4))) {
            throw new IllegalArgumentException(
                    String.format("%s, %s không khớp với mã bưu chính %s.", district, province, postal_code));
        }
        return true;
    }

    public boolean checkWardsOcupation(String province, String districs, List<String> ward_arr) {
        for (final String ward : ward_arr) {
            final Optional<Ward> resultFidingWard = wardRepository.findByProvinceAndDistrictAndWard(province, districs,
                    ward);
            if (resultFidingWard.isEmpty()) {
                throw new IllegalArgumentException(String.format("%s không tồn tại", ward));
            }
            final String occupierAgencyId = resultFidingWard.get().getAgencyId();
            if (occupierAgencyId != null && Pattern.matches(regexPersonnel, occupierAgencyId)) {
                throw new IllegalStateException(
                        String.format("%s đã được quản lý bởi đại lý/bưu cục có mã đại lý %s", ward, occupierAgencyId));
            }
        }
        return true;
    }

    public List<String> createTablesForAgency(String postalCode) throws Exception {

        return agencyRepository.createTablesForAgency(postalCode);

    }

    public Response dropTablesForAgency(String postalCode) {
        try {
            return agencyRepository.dropTablesForAgency(postalCode);
        } catch (Exception e) {
            return new Response<>(true, "Da co loi trong he thong", null);
        }
    }

    public Response locateAgencyInArea(int choice, String province, String district, List<String> wards,
            String agencyId, String postalCode) {
        try {
            return agencyRepository.locateAgencyInArea(choice, province, district, wards, agencyId, postalCode);
        } catch (Exception e) {
            return new Response<>(true, "Da co loi trong he thong", null);
        }
    }

    public Optional<Agency> getOneAgency(Agency agency) {
        return checkExistAgency(agency);
    }

    public List<Agency> getManyAgencies(Agency template_agency, Integer pages, Integer rows) {
        try {

            ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
            Example<Agency> example = Example.of(template_agency, matcher);

            int limit = (rows != null && rows >= 1) ? rows : 3;
            int offset = (pages != null) ? pages * limit : 0;
            // havent implemented the pagination yet
            Pageable pageable = PageRequest.of(offset, limit);

            return agencyRepository.findAll(example);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;

        }
    }

    //
    public Agency createNewAgency(Agency agency) throws Exception {
        Optional<Agency> existingAgency = checkExistAgency(agency);
        if (existingAgency.isPresent()) {
            throw new Exception("Agency already exists");
        }
        try {
            agencyRepository.save(agency);
            return agency;
        } catch (Exception e) {
            throw new Exception("Error creating agency: " + e.getMessage());
        }
    }
    //
    // public Response updateAgency(Agency agency) {
    // Optional<Agency> existingAgency = checkExistAgency(agency);
    // if (!existingAgency.isPresent()) {
    // return new Response<>(true, "Agency does not exist", null);
    // }
    // try {
    // agencyRepository.save(agency);
    // return new Response<>(false, "Agency updated successfully", agency);
    // } catch (Exception e) {
    // return new Response<>(true, "Error updating agency: " + e.getMessage(),
    // null);
    // }
    // }
    //
    // public Response deleteAgency(Agency agency) {
    // Optional<Agency> existingAgency = checkExistAgency(agency);
    // if (!existingAgency.isPresent()) {
    // return new Response<>(true, "Agency does not exist", null);
    // }
    // try {
    // agencyRepository.delete(existingAgency.get());
    // return new Response<>(false, "Agency deleted successfully",
    // existingAgency.get());
    // } catch (Exception e) {
    // return new Response<>(true, "Error deleting agency: " + e.getMessage(),
    // null);
    // }
    // }

    // public Response updatePassword

}
