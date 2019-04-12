#import "Open.h"











@implementation Open



CDVPluginResult* commandResult = nil;



CDVInvokedUrlCommand* anotherCommand=nil;



- (void)previewControllerWillDismiss:(QLPreviewController *)controller{

    commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"closeSuccess"];

    [self.commandDelegate sendPluginResult:commandResult callbackId:anotherCommand.callbackId];

};

/**

 *  open

 *

 *  @param command An array of arguments passed from javascript

 */





- (void)open:(CDVInvokedUrlCommand *)command {

    

    anotherCommand = command;

    dispatch_async(dispatch_get_main_queue(), ^{

        // Call UI related operations

        

        

        NSArray  *arguments = command.arguments;

        NSString *fileId;

        NSString  *fileName;

        NSString  *extension;

        if (!arguments || [arguments count] < 2) {

            NSLog(@"#### setTagsWithAlias param is less");

            return ;

        }else{

            fileName  = arguments[0];

            fileId = arguments[1];

            extension =  [fileName pathExtension];  //aaa.doc -> doc

        }

        NSString *storeName =[NSString stringWithFormat:@"%@.%@",fileId,extension];

        NSLog(@"####  fileId is %@, fileName is %@",fileId,fileName);

        NSString *tmpDir=[NSHomeDirectory() stringByAppendingPathComponent:@"tmp/files"];

        NSString *newPath = [tmpDir stringByAppendingPathComponent:[NSString stringWithFormat:@"/%@",storeName]];

        if (newPath != nil && [newPath length] > 0) {

            

            NSURL *url = [NSURL fileURLWithPath:newPath];

            NSError *err;

            

            if (url.isFileURL &&

                [url checkResourceIsReachableAndReturnError:&err] == YES) {

                

                self.fileUrl = url;

               

                

                QLPreviewController *previewCtrl = [[QLPreviewController alloc] init];

                previewCtrl.delegate = self;

                previewCtrl.dataSource = self;

                

                [previewCtrl.navigationItem setRightBarButtonItem:nil];

                

                [self.viewController presentViewController:previewCtrl animated:YES completion:nil];

                

                NSLog(@"cordova.disusered.open - Success!");

                commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK

                                                  messageAsString:@"openSuccess"];
                //[self.commandDelegate sendPluginResult:commandResult callbackId:command.callbackId];

                

            } else {

                NSLog(@"cordova.disusered.open - Invalid file URL wq   ");

                commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:0];

                [self.commandDelegate sendPluginResult:commandResult callbackId:command.callbackId];



            }

        } else {

            NSLog(@"cordova.disusered.open - Missing URL argument");

            commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];

            [self.commandDelegate sendPluginResult:commandResult callbackId:command.callbackId];



        }

        

    });

}



-(void)isFileExist:(CDVInvokedUrlCommand*)command{

    NSArray  *arguments = command.arguments;

    NSString *fileId;

    NSString  *fileName;

    NSString  *extension;

    if (!arguments || [arguments count] < 2) {

        NSLog(@"#### setTagsWithAlias param is less");

        return ;

    }else{

        fileName  = arguments[0];

        fileId  = arguments[1];

        extension =  [fileName pathExtension];  //aaa.doc -> doc

    }

    NSString *storeName =[NSString stringWithFormat:@"%@.%@",fileId,extension];

    NSLog(@"####  fileId is %@, fileName is %@",fileId,fileName);

    NSString *tmpDir=[NSHomeDirectory() stringByAppendingPathComponent:@"tmp/files"];

    NSString *storFile = [tmpDir stringByAppendingPathComponent:[NSString stringWithFormat:@"/%@",storeName]];

    

    NSFileManager *fileManager = [NSFileManager defaultManager];

    BOOL result = [fileManager fileExistsAtPath:storFile];

    CDVPluginResult* pluginResult = nil;

    

    if (result) {

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];

    } else {

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];

    }

    

    

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

    

}

#pragma - QLPreviewControllerDataSource Protocol



- (NSInteger)numberOfPreviewItemsInPreviewController:

(QLPreviewController *)controller {

    return 1;

}



- (id<QLPreviewItem>)previewController:(QLPreviewController *)controller

                    previewItemAtIndex:(NSInteger)index {

    return self;

}



#pragma - QLPreviewItem Protocol



- (NSURL *)previewItemURL {

    return self.fileUrl;

}



@end