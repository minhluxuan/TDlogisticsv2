package project.tdlogistics.users.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import project.tdlogistics.users.configurations.MyBeanUtils;
import project.tdlogistics.users.entities.Staff;
import project.tdlogistics.users.repositories.StaffRepository;

@Service
public class StaffService {
    @Autowired
    private StaffRepository staffRepository;

    public Optional<Staff> checkExistStaff(Staff criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Staff> example = Example.of(criteria, matcher);
        List<Staff> customers = staffRepository.findAll(example);
        if (customers.size() == 1) {
            final Staff staff = customers.get(0);
            staff.getAccount().setPassword(null);
            return Optional.of(staff);
        } else {
            return Optional.empty();
        }
    }

    public Staff createNewStaff(Staff info) {
        staffRepository.save(info);
        // info.getAccount().setPassword(null);
        return info;
    }

    public Optional<Staff> getStaffById(String id) {
        return staffRepository.findById(id);
    }

    public Optional<Staff> getStaffByCccd(String cccd) {
        return staffRepository.findOneByCccd(cccd);
    }

    public List<Staff> getStaffs(Staff criteria) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Staff> example = Example.of(criteria, matcher);
        final List<Staff> staffs = staffRepository.findAll(example);
        staffs.forEach((staff) -> {
            staff.getAccount().setPassword(null);
        });

        return staffs;
    }

    public Staff updateStaffInfo(String id, Staff info) {
        Optional<Staff> staffOptional = staffRepository.findById(id);
        if (staffOptional.isEmpty()) {
            return null; // Or throw an exception if staff not found
        }

        Staff existingStaff = staffOptional.get();

        // Copy non-null properties from info to existingStaff
        MyBeanUtils.copyNonNullProperties(info, existingStaff);

        // Save the updated staff entity
        staffRepository.save(existingStaff);

        // Return the updated staff entity
        return staffRepository.findById(id).get();
    }

    public void deleteStaff(String id) {
        staffRepository.deleteById(id);
    }
}
