package archangeldlt.contract;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.0.1.
 */
public class Archangel extends Contract {
    private static final String BINARY = "0x60806040523480156200001157600080fd5b506060336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506040805190810160405280600881526020017f636f6e7472616374000000000000000000000000000000000000000000000000815250905080600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000209080519060200190620000e1929190620001dd565b507f3e7a939fe856e16ddb4b017165d29dcf6d87c6bc6115a55d7895ec3e7d4c66a16000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1682604051808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200180602001828103825283818151815260200191508051906020019080838360005b838110156200019a5780820151818401526020810190506200017d565b50505050905090810190601f168015620001c85780820380516001836020036101000a031916815260200191505b50935050505060405180910390a1506200028c565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200022057805160ff191683800117855562000251565b8280016001018555821562000251579182015b828111156200025057825182559160200191906001019062000233565b5b50905062000260919062000264565b5090565b6200028991905b80821115620002855760008160009055506001016200026b565b5090565b90565b6112ae806200029c6000396000f300608060405260043610610078576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806350108e641461007d57806397128e0014610136578063bbd9a5fa14610191578063d246d0b9146101ec578063e074bb47146102af578063f641090c146102f2575b600080fd5b34801561008957600080fd5b506100ac6004803603810190808035600019169060200190929190505050610345565b60405180806020018360001916600019168152602001828103825284818151815260200191508051906020019080838360005b838110156100fa5780820151818401526020810190506100df565b50505050905090810190601f1680156101275780820380516001836020036101000a031916815260200191505b50935050505060405180910390f35b34801561014257600080fd5b50610177600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610416565b604051808215151515815260200191505060405180910390f35b34801561019d57600080fd5b506101ea600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001919091929391929390505050610476565b005b3480156101f857600080fd5b5061022560048036038101908080359060200190820180359060200191909192939192939050505061068b565b60405180806020018360001916600019168152602001828103825284818151815260200191508051906020019080838360005b83811015610273578082015181840152602081019050610258565b50505050905090810190601f1680156102a05780820380516001836020036101000a031916815260200191505b50935050505060405180910390f35b3480156102bb57600080fd5b506102f0600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610768565b005b3480156102fe57600080fd5b50610343600480360381019080803590602001908201803590602001919091929391929390803590602001908201803590602001919091929391929390505050610aa0565b005b60606000806002600085600019166000191681526020019081526020016000209050806000018160010154818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104055780601f106103da57610100808354040283529160200191610405565b820191906000526020600020905b8154815290600101906020018083116103e857829003601f168201915b505050505091509250925050915091565b600080600360008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002080546001816001161561010002031660029004905014159050919050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156104d157600080fd5b3073ffffffffffffffffffffffffffffffffffffffff166397128e00846040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b15801561056c57600080fd5b505af1158015610580573d6000803e3d6000fd5b505050506040513d602081101561059657600080fd5b8101908080519060200190929190505050156105b157610686565b8181600360008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002091906105ff92919061110e565b507f3e7a939fe856e16ddb4b017165d29dcf6d87c6bc6115a55d7895ec3e7d4c66a1838383604051808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001806020018281038252848482818152602001925080828437820191505094505050505060405180910390a15b505050565b6060600080600185856040518083838082843782019150509250505090815260200160405180910390209050806000018160010154818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156107555780601f1061072a57610100808354040283529160200191610755565b820191906000526020600020905b81548152906001019060200180831161073857829003601f168201915b5050505050915092509250509250929050565b60606000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156107c557600080fd5b3073ffffffffffffffffffffffffffffffffffffffff166397128e00836040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b15801561086057600080fd5b505af1158015610874573d6000803e3d6000fd5b505050506040513d602081101561088a57600080fd5b810190808051906020019092919050505015156108a657610a9c565b600360008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109795780601f1061094e57610100808354040283529160200191610979565b820191906000526020600020905b81548152906001019060200180831161095c57829003601f168201915b50505050509050600360008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006109cb919061118e565b7f90a815a6c080c0b52322c5faf7e1044a5a23ba1573f10e31bb37bda8c284f1488282604051808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200180602001828103825283818151815260200191508051906020019080838360005b83811015610a60578082015181840152602081019050610a45565b50505050905090810190601f168015610a8d5780820380516001836020036101000a031916815260200191505b50935050505060405180910390a15b5050565b600080600080600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208054600181600116156101000203166002900490501415610b68577faeaa938e44e461836fba3707e66ee1896fb303af1033946bce82fe43c8d4d4d033604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390a1610d52565b610ba387878080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050610d5b565b92506001878760405180838380828437820191505092505050908152602001604051809103902090508484826000019190610bdf92919061110e565b5082816001018160001916905550816000191683600019161415610ca9577f8c8a2268d0f6b369e3217b2e09b6506c0ef0fd52b2b9d13c7973bd256d9b9ae13388888888604051808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018060200180602001838103835287878281815260200192508082843782019150508381038252858582818152602001925080828437820191505097505050505050505060405180910390a1610d51565b7fd3f97370b3ab0cd8aebe79d3b4fc23ab3287930b0b23578aeafb2253eb4e24e63388888888604051808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018060200180602001838103835287878281815260200192508082843782019150508381038252858582818152602001925080828437820191505097505050505050505060405180910390a15b5b50505050505050565b60008060008060006001866040518082805190602001908083835b602083101515610d9b5780518252602082019150602081019050602083039250610d76565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902092506000836000018054600181600116156101000203166002900490501415610df957839450610f42565b856040516020018082805190602001908083835b602083101515610e325780518252602082019150602081019050602083039250610e0d565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040516020818303038152906040526040518082805190602001908083835b602083101515610e9b5780518252602082019150602081019050602083039250610e76565b6001836020036101000a03801982511681845116808217855250505050505090500191505060405180910390209150610ed382610f4b565b9050826000016002600084600019166000191681526020019081526020016000206000019080546001816001161561010002031660029004610f169291906111d6565b508060026000846000191660001916815260200190815260200160002060010181600019169055508194505b50505050919050565b60008060008060026000866000191660001916815260200190815260200160002091506000826000018054600181600116156101000203166002900490501415610f9757829350611106565b848260000183600101546040516020018084600019166000191681526020018380546001816001161561010002031660029004801561100d5780601f10610feb57610100808354040283529182019161100d565b820191906000526020600020905b815481529060010190602001808311610ff9575b5050826000191660001916815260200193505050506040516020818303038152906040526040518082805190602001908083835b6020831015156110665780518252602082019150602081019050602083039250611041565b6001836020036101000a038019825116818451168082178552505050505050905001915050604051809103902090508160000160026000836000191660001916815260200190815260200160002060000190805460018160011615610100020316600290046110d69291906111d6565b50816001015460026000836000191660001916815260200190815260200160002060010181600019169055508093505b505050919050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061114f57803560ff191683800117855561117d565b8280016001018555821561117d579182015b8281111561117c578235825591602001919060010190611161565b5b50905061118a919061125d565b5090565b50805460018160011615610100020316600290046000825580601f106111b457506111d3565b601f0160209004906000526020600020908101906111d2919061125d565b5b50565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061120f578054855561124c565b8280016001018555821561124c57600052602060002091601f016020900482015b8281111561124b578254825591600101919060010190611230565b5b509050611259919061125d565b5090565b61127f91905b8082111561127b576000816000905550600101611263565b5090565b905600a165627a7a7230582018ecd621e694aebbe70aefba5be951dd42f689fcc674f797393bd13fef4103950029";

    public static final String FUNC_HASPERMISSION = "hasPermission";

    public static final String FUNC_GRANTPERMISSION = "grantPermission";

    public static final String FUNC_REMOVEPERMISSION = "removePermission";

    public static final String FUNC_STORE = "store";

    public static final String FUNC_FETCH = "fetch";

    public static final String FUNC_FETCHPREVIOUS = "fetchPrevious";

    public static final Event REGISTRATION_EVENT = new Event("Registration", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event UPDATE_EVENT = new Event("Update", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event NOWRITEPERMISSION_EVENT = new Event("NoWritePermission", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final Event PERMISSIONGRANTED_EVENT = new Event("PermissionGranted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event PERMISSIONREMOVED_EVENT = new Event("PermissionRemoved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("4", "0xb5ccf2f1d5eb411705d02f59f6b3d694268cfdad");
        _addresses.put("3151", "0xdfacfbd00a7cefc913a46c32f824f1a78ffe1417");
        _addresses.put("53419", "0xf68e8324254895bdc712355aa2aa2e5f557818fd");
    }

    @Deprecated
    protected Archangel(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Archangel(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Archangel(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Archangel(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<RegistrationEventResponse> getRegistrationEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REGISTRATION_EVENT, transactionReceipt);
        ArrayList<RegistrationEventResponse> responses = new ArrayList<RegistrationEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RegistrationEventResponse typedResponse = new RegistrationEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._key = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse._payload = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RegistrationEventResponse> registrationEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RegistrationEventResponse>() {
            @Override
            public RegistrationEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REGISTRATION_EVENT, log);
                RegistrationEventResponse typedResponse = new RegistrationEventResponse();
                typedResponse.log = log;
                typedResponse._addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._key = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse._payload = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RegistrationEventResponse> registrationEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REGISTRATION_EVENT));
        return registrationEventFlowable(filter);
    }

    public List<UpdateEventResponse> getUpdateEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UPDATE_EVENT, transactionReceipt);
        ArrayList<UpdateEventResponse> responses = new ArrayList<UpdateEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UpdateEventResponse typedResponse = new UpdateEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._key = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse._payload = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<UpdateEventResponse> updateEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, UpdateEventResponse>() {
            @Override
            public UpdateEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UPDATE_EVENT, log);
                UpdateEventResponse typedResponse = new UpdateEventResponse();
                typedResponse.log = log;
                typedResponse._addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._key = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse._payload = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<UpdateEventResponse> updateEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UPDATE_EVENT));
        return updateEventFlowable(filter);
    }

    public List<NoWritePermissionEventResponse> getNoWritePermissionEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NOWRITEPERMISSION_EVENT, transactionReceipt);
        ArrayList<NoWritePermissionEventResponse> responses = new ArrayList<NoWritePermissionEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NoWritePermissionEventResponse typedResponse = new NoWritePermissionEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NoWritePermissionEventResponse> noWritePermissionEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, NoWritePermissionEventResponse>() {
            @Override
            public NoWritePermissionEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NOWRITEPERMISSION_EVENT, log);
                NoWritePermissionEventResponse typedResponse = new NoWritePermissionEventResponse();
                typedResponse.log = log;
                typedResponse._addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<NoWritePermissionEventResponse> noWritePermissionEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NOWRITEPERMISSION_EVENT));
        return noWritePermissionEventFlowable(filter);
    }

    public List<PermissionGrantedEventResponse> getPermissionGrantedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PERMISSIONGRANTED_EVENT, transactionReceipt);
        ArrayList<PermissionGrantedEventResponse> responses = new ArrayList<PermissionGrantedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PermissionGrantedEventResponse typedResponse = new PermissionGrantedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._name = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<PermissionGrantedEventResponse> permissionGrantedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, PermissionGrantedEventResponse>() {
            @Override
            public PermissionGrantedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PERMISSIONGRANTED_EVENT, log);
                PermissionGrantedEventResponse typedResponse = new PermissionGrantedEventResponse();
                typedResponse.log = log;
                typedResponse._addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._name = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<PermissionGrantedEventResponse> permissionGrantedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PERMISSIONGRANTED_EVENT));
        return permissionGrantedEventFlowable(filter);
    }

    public List<PermissionRemovedEventResponse> getPermissionRemovedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PERMISSIONREMOVED_EVENT, transactionReceipt);
        ArrayList<PermissionRemovedEventResponse> responses = new ArrayList<PermissionRemovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PermissionRemovedEventResponse typedResponse = new PermissionRemovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._name = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<PermissionRemovedEventResponse> permissionRemovedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, PermissionRemovedEventResponse>() {
            @Override
            public PermissionRemovedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PERMISSIONREMOVED_EVENT, log);
                PermissionRemovedEventResponse typedResponse = new PermissionRemovedEventResponse();
                typedResponse.log = log;
                typedResponse._addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._name = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<PermissionRemovedEventResponse> permissionRemovedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PERMISSIONREMOVED_EVENT));
        return permissionRemovedEventFlowable(filter);
    }

    public RemoteCall<Boolean> hasPermission(String addr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_HASPERMISSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> grantPermission(String addr, String name) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GRANTPERMISSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr), 
                new org.web3j.abi.datatypes.Utf8String(name)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> removePermission(String addr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REMOVEPERMISSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> store(String key, String payload) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_STORE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(key), 
                new org.web3j.abi.datatypes.Utf8String(payload)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple2<String, byte[]>> fetch(String key) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FETCH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(key)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Bytes32>() {}));
        return new RemoteCall<Tuple2<String, byte[]>>(
                new Callable<Tuple2<String, byte[]>>() {
                    @Override
                    public Tuple2<String, byte[]> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, byte[]>(
                                (String) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<Tuple2<String, byte[]>> fetchPrevious(byte[] key) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FETCHPREVIOUS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(key)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Bytes32>() {}));
        return new RemoteCall<Tuple2<String, byte[]>>(
                new Callable<Tuple2<String, byte[]>>() {
                    @Override
                    public Tuple2<String, byte[]> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, byte[]>(
                                (String) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue());
                    }
                });
    }

    @Deprecated
    public static Archangel load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Archangel(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Archangel load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Archangel(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Archangel load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Archangel(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Archangel load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Archangel(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Archangel> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Archangel.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<Archangel> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Archangel.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Archangel> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Archangel.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Archangel> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Archangel.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class RegistrationEventResponse {
        public Log log;

        public String _addr;

        public String _key;

        public String _payload;
    }

    public static class UpdateEventResponse {
        public Log log;

        public String _addr;

        public String _key;

        public String _payload;
    }

    public static class NoWritePermissionEventResponse {
        public Log log;

        public String _addr;
    }

    public static class PermissionGrantedEventResponse {
        public Log log;

        public String _addr;

        public String _name;
    }

    public static class PermissionRemovedEventResponse {
        public Log log;

        public String _addr;

        public String _name;
    }
}
