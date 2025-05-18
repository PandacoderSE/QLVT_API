package DATN.ITDeviceManagement.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public interface IExcelService {
    void importFromExcel(InputStream is);
    ByteArrayInputStream loadSelectedDevices(List<Long> deviceIds);
}
